package com.hurence.logisland.agent.rest.api;


import com.hurence.logisland.agent.rest.model.Topic;

import com.hurence.logisland.kafka.registry.KafkaRegistry;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-03-03T16:47:02.913+01:00")
public abstract class TopicsApiService {

    protected final KafkaRegistry kafkaRegistry;

    public TopicsApiService(KafkaRegistry kafkaRegistry) {
        this.kafkaRegistry = kafkaRegistry;
    }
        public abstract Response addNewTopic(Topic body,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response checkTopicKeySchemaCompatibility(String body,String topicId,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response checkTopicValueSchemaCompatibility(String topicId,String body,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response deleteTopic(String topicId,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response getAllTopics(SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response getTopic(String topicId,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response getTopicKeySchema(String topicId,String version,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response getTopicValueSchema(String topicId,String version,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response updateTopic(Topic body,String topicId,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response updateTopicKeySchema(String body,String topicId,SecurityContext securityContext)
        throws NotFoundException;
        public abstract Response updateTopicValueSchema(String body,String topicId,SecurityContext securityContext)
        throws NotFoundException;
    }
