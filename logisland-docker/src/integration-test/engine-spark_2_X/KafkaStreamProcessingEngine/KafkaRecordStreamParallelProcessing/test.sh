#!/usr/bin/env bash

source /shared/util.sh

# main class that test
main() {
    echo "initializing variables"
    #SET CONSTANT AND ENVIRONMENT VARIABLES
    CONF_FILE="logisland-config.yml"
    INPUT_FILE_PATH="/conf/input"
    EXPECTED_FILE_PATH="/conf/input"
    KAFKA_INPUT_TOPIC="logisland_raw"
    KAFKA_OUTPUT_TOPIC="logisland_events"
    KAFKA_ERROR_TOPIC="logisland_errors"
    #DEBUG="set -x"#Comment if you do not want debug

    KAFKA_BROKER_HOST="kafka"
    KAFKA_BROKER_PORT="9092"
    KAFKA_BROKER_URL="${KAFKA_BROKER_HOST}:${KAFKA_BROKER_PORT}"
    default_value KAFKACAT_BIN "/usr/local/bin/kafkacat"

    export KAFKA_BROKERS="${KAFKA_BROKER_HOST}:${KAFKA_BROKER_PORT}"
    export ZK_QUORUM="zookeeper:2181"

    echo "starting logisland with ${CONF_FILE}"
    nohup bin/logisland.sh --conf /conf/${CONF_FILE} & > ${CONF_FILE}_job.log
    echo "waiting 20 seconds for job to initialize"
    sleep 20

    echo "some check before sending data"
    file_present "${INPUT_FILE_PATH}"
    file_present "${EXPECTED_FILE_PATH}"

    # Ensure kafka topic is created before sending data.
    lookup_kafka_topics ${KAFKA_INPUT_TOPIC} ${KAFKA_OUTPUT_TOPIC} ${KAFKA_ERROR_TOPIC}

    # Sends data to kafka.
    echo "sending input in kafka"
    EXPECTED_DOCS_COUNT=$(${DEBUG}; wc "${INPUT_FILE_PATH}" | awk '{print $1}')
    echo "EXPECTED_DOCS_COUNT ${EXPECTED_DOCS_COUNT}"
    echo "cat ${INPUT_FILE_PATH} | ${KAFKA_HOME}/bin/kafka-console-producer.sh --broker-list ${KAFKA_BROKER_URL} --topic ${KAFKA_INPUT_TOPIC}"
    cat ${INPUT_FILE_PATH} | ${KAFKA_HOME}/bin/kafka-console-producer.sh --broker-list ${KAFKA_BROKER_URL} --topic ${KAFKA_INPUT_TOPIC}
    abort_if "${?}" "Unable to send input ${INPUT_FILE_PATH}  into ${KAFKA_INPUT_TOPIC}. Aborting."
    sleep 10

    echo "check that we received it"

    REAL_DOCS_COUNT=$( \
    ${KAFKA_HOME}/bin/kafka-console-consumer.sh --topic ${KAFKA_OUTPUT_TOPIC} \
    --zookeeper ${ZK_QUORUM} \
    --from-beginning \
    --timeout-ms 10000 \
    | grep '\"id\" :' \
    | wc -l \
    )
    abort_if "${?}" "Unable to count events in ${KAFKA_OUTPUT_TOPIC}. Aborting."

    echo "sent ${EXPECTED_DOCS_COUNT} inputs and got ${REAL_DOCS_COUNT} outputs"
    if [[ ${EXPECTED_DOCS_COUNT} == ${REAL_DOCS_COUNT} ]]
    then
        exit 0
    else
        exit 1
    fi
}

main $@


