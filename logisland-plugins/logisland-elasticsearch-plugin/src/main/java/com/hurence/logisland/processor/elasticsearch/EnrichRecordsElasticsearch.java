package com.hurence.logisland.processor.elasticsearch;


import com.hurence.logisland.annotation.documentation.CapabilityDescription;
import com.hurence.logisland.annotation.documentation.Tags;
import com.hurence.logisland.component.PropertyDescriptor;
import com.hurence.logisland.expressionlanguage.InterpreterEngineException;
import com.hurence.logisland.processor.ProcessContext;
import com.hurence.logisland.service.elasticsearch.multiGet.InvalidMultiGetQueryRecordException;
import com.hurence.logisland.service.elasticsearch.multiGet.MultiGetQueryRecord;
import com.hurence.logisland.service.elasticsearch.multiGet.MultiGetQueryRecordBuilder;
import com.hurence.logisland.service.elasticsearch.multiGet.MultiGetResponseRecord;
import com.hurence.logisland.record.*;
import com.hurence.logisland.validator.StandardValidators;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tags({"elasticsearch"})
@CapabilityDescription("Enrich input records with content indexed in elasticsearch using multiget queries.\n" +
        "Each incoming record must be possibly enriched with information stored in elasticsearch. \n" +
        "The plugin properties are :\n" +
        "- es.index (String)            : Name of the elasticsearch index on which the multiget query will be performed. This field is mandatory and should not be empty, otherwise an error output record is sent for this specific incoming record.\n" +
        "- record.key (String)          : Name of the field in the input record containing the id to lookup document in elastic search. This field is mandatory.\n" +
        "- es.key (String)              : Name of the elasticsearch key on which the multiget query will be performed. This field is mandatory.\n" +
        "- includes (ArrayList<String>) : List of patterns to filter in (include) fields to retrieve. Supports wildcards. This field is not mandatory.\n" +
        "- excludes (ArrayList<String>) : List of patterns to filter out (exclude) fields to retrieve. Supports wildcards. This field is not mandatory.\n" +
        "\n" +
        "Each outcoming record holds at least the input record plus potentially one or more fields coming from of one elasticsearch document."
)
public class EnrichRecordsElasticsearch extends AbstractElasticsearchProcessor {
    private static Logger logger = LoggerFactory.getLogger(EnrichRecordsElasticsearch.class);

    public static final PropertyDescriptor RECORD_KEY_FIELD = new PropertyDescriptor.Builder()
            .name("record.key")
            .description("The name of field in the input record containing the document id to use in ES multiget query")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor ES_INDEX_FIELD = new PropertyDescriptor.Builder()
            .name("es.index")
            .description("The name of the ES index to use in multiget query. ")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();


    public static final PropertyDescriptor ES_TYPE_FIELD = new PropertyDescriptor.Builder()
            .name("es.type")
            .description("The name of the ES type to use in multiget query. ")
            .required(false)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor ES_INCLUDES_FIELD = new PropertyDescriptor.Builder()
            .name("es.includes.field")
            .description("The name of the ES fields to include in the record.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .defaultValue("*")
            .build();

    public static final PropertyDescriptor ES_EXCLUDES_FIELD = new PropertyDescriptor.Builder()
            .name("es.excludes.field")
            .description("The name of the ES fields to exclude.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .defaultValue("N/A")
            .build();

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {

        List<PropertyDescriptor> props = new ArrayList<>();
        props.add(ELASTICSEARCH_CLIENT_SERVICE);
        props.add(RECORD_KEY_FIELD);
        props.add(ES_INDEX_FIELD);
        props.add(ES_TYPE_FIELD);
        props.add(ES_INCLUDES_FIELD);
        props.add(ES_EXCLUDES_FIELD);

        return Collections.unmodifiableList(props);
    }

    /**
     * process events
     *
     * @param context
     * @param records
     * @return
     */
    @Override
    public Collection<Record> process(final ProcessContext context, final Collection<Record> records) {

        List<Record> outputRecords = new ArrayList<>();
        List<Pair<Record,String>> recordsToEnrich = new ArrayList<>();

        if (records.size() != 0) {
            String includesFieldName = context.getPropertyValue(ES_INCLUDES_FIELD).asString();
            String excludesFieldName = context.getPropertyValue(ES_EXCLUDES_FIELD).asString();

            // Includes :
            String[] includesArray = null;
            if ((includesFieldName != null) && (!includesFieldName.isEmpty())) {
                includesArray = includesFieldName.split("\\s*,\\s*");
            }

            // Excludes :
            String[] excludesArray = null;
            if ((excludesFieldName != null) && (!excludesFieldName.isEmpty())) {
                excludesArray = excludesFieldName.split("\\s*,\\s*");
            }

            //List<MultiGetQueryRecord> multiGetQueryRecords = new ArrayList<>();
            MultiGetQueryRecordBuilder mgqrBuilder = new MultiGetQueryRecordBuilder();

            mgqrBuilder.excludeFields(excludesArray);
            mgqrBuilder.includeFields(includesArray);

            List<MultiGetResponseRecord> multiGetResponseRecords = null;
            // HashSet<String> ids = new HashSet<>(); // Use a Set to avoid duplicates



            for (Record record : records) {

                String recordKeyName = null;
                String indexName = null;
                String typeName = null;

                try {
                    recordKeyName = context.getPropertyValue(RECORD_KEY_FIELD).evaluate(record).asString();
                    indexName = context.getPropertyValue(ES_INDEX_FIELD).evaluate(record).asString();
                    typeName = context.getPropertyValue(ES_TYPE_FIELD).evaluate(record).asString();
                } catch (Throwable t) {
                    record.setStringField(FieldDictionary.RECORD_ERRORS, "Failure in executing EL. Error: " + t.getMessage());
                    logger.error("Cannot interpret EL : " + record, t);
                }

                if (recordKeyName != null) {
                    try {
                        mgqrBuilder.add(indexName, typeName, recordKeyName);
                        recordsToEnrich.add(new ImmutablePair(record, asUniqueKey(indexName, typeName, recordKeyName)));
                    } catch (Throwable t) {
                        record.setStringField(FieldDictionary.RECORD_ERRORS, "Can not request ElasticSearch with " + indexName + " "  + typeName + " " + recordKeyName);
                        outputRecords.add(record);
                    }
                } else {
                    record.setStringField(FieldDictionary.RECORD_ERRORS, "Interpreted EL returned null for recordKeyName");
                    outputRecords.add(record);
                    logger.error("Interpreted EL returned null for recordKeyName");
                }
            }

            try {
                List<MultiGetQueryRecord> mgqrs = mgqrBuilder.build();

                for (MultiGetQueryRecord mgqr : mgqrs) {
                    System.out.println(mgqr);
                }

                multiGetResponseRecords = elasticsearchClientService.multiGet(mgqrs);
            } catch (InvalidMultiGetQueryRecordException e ){
                // should never happen
                e.printStackTrace();
                // TODO : Fix above
            }

            if (multiGetResponseRecords == null || multiGetResponseRecords.isEmpty()) {
                return records;
            }


             for (MultiGetResponseRecord mgrr : multiGetResponseRecords) {
                    System.out.println(mgrr);
                }


            // Transform the returned documents from ES in a Map
            Map<String, MultiGetResponseRecord> responses = multiGetResponseRecords.
                    stream().
                    collect(Collectors.toMap(EnrichRecordsElasticsearch::asUniqueKey, Function.identity()));

            recordsToEnrich.forEach(recordToEnrich -> {

                Pair<Record,String> pair = recordToEnrich;
                Record outputRecord = pair.getLeft();

                // TODO: should probably store the resulting recordKeyName during previous invocation above
                String key = pair.getRight();
                MultiGetResponseRecord responseRecord = responses.get(key);
                if ((responseRecord != null) && (responseRecord.getRetrievedFields() != null)) {
                    responseRecord.getRetrievedFields().forEach((k, v) -> {
                        outputRecord.setStringField(k.toString(), v.toString());
                    });
                }
                outputRecords.add(outputRecord);

            });
        }
        return outputRecords;
    }

    private static String asUniqueKey(MultiGetResponseRecord mgrr) {
        return asUniqueKey(mgrr.getIndexName(), mgrr.getTypeName(), mgrr.getDocumentId());
    }

    private static String asUniqueKey(String indexName, String typeName, String documentId) {
        StringBuilder sb = new StringBuilder();
        sb.append(indexName)
                .append(":")
                .append(typeName)
                .append(":")
                .append(documentId);
        return sb.toString();
    }

}
