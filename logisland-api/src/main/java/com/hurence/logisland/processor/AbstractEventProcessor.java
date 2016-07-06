package com.hurence.logisland.processor;

import com.hurence.logisland.components.AbstractConfigurableComponent;
import com.hurence.logisland.components.PropertyDescriptor;
import com.hurence.logisland.validators.StandardValidators;

/**
 * Created by tom on 01/07/16.
 */
public abstract class AbstractEventProcessor extends AbstractConfigurableComponent implements EventProcessor {

    public static final String DEAD_LETTER_TOPIC = "logisland-dead-letter-queue";

    //private final EventStreamConfig config;

    public static final PropertyDescriptor OUTPUT_TOPICS = new PropertyDescriptor.Builder()
            .name("kafka.output.topics")
            .description("Sets the output Kafka topic name")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .defaultValue(DEAD_LETTER_TOPIC)
            .build();

    public static final PropertyDescriptor INPUT_TOPICS = new PropertyDescriptor.Builder()
            .name("kafka.input.topics")
            .description("Sets the input Kafka topic name")
            .required(true)
            .defaultValue(DEAD_LETTER_TOPIC)
            .build();

    public static final PropertyDescriptor ERROR_TOPICS = new PropertyDescriptor.Builder()
            .name("kafka.error.topics")
            .description("Sets the error topics Kafka topic name")
            .required(true)
            .defaultValue(DEAD_LETTER_TOPIC)
            .build();
}