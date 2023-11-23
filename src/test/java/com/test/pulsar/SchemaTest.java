package com.test.pulsar;

import com.test.pulsar.bo.User;
import com.test.pulsar.bo.User1;
import com.test.pulsar.bo.User2;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.api.schema.GenericRecord;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.junit.jupiter.api.Test;

class SchemaTest extends BasicTest {

    @Test
    void test1() throws PulsarClientException {
        var topic = topicPrefix + "topic-schema";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(Schema.JSON(User.class)).topic(topic).create();
        var consumer = pulsarClient.newConsumer(JSONSchema.of(User.class)).topic(topic)
                .subscriptionName("test-schema-sub")
                // SubscriptionInitialPosition.Earliest: Consumer starts consuming messages from the earliest available message in the topic.
                // SubscriptionInitialPosition.Latest: Consumer starts consuming messages from the latest available message in the topic.
                // Choose the appropriate initial position based on your use case. If you need to process all messages in a topic,
                // including those produced before the consumer was created, you would use Earliest.
                // If you only want to process messages produced after the consumer was created, you would use Latest.
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscribe();

        User user = new User("Tom", 28);
        producer.send(user);

        var msg = consumer.receive();
        var userRec = msg.getValue();

        assert userRec.age() == 28 && userRec.name().equals("Tom");
    }


    /**
     * Avro does not natively support Java records!
     * Avro relies on code generation, and it typically works well with classes generated from a specific schema definition.
     * Java records, which were introduced in Java 16, are a feature for creating simple,
     * immutable data-carrying classes, but they may not be directly compatible with Avro's expectations for class generation.
     * @throws PulsarClientException
     */
    @Test
    void testAVRO() throws PulsarClientException {
        var topic = topicPrefix + "topic-schema-avro";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(Schema.AVRO(User1.class)).topic(topic).create();
        var consumer = pulsarClient.newConsumer(Schema.AVRO(User1.class)).topic(topic)
                .subscriptionName("test-schema-sub-avro")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscribe();

        var user = User1.builder().name("Tom").age(28).build();
        producer.send(user);

        var msg = consumer.receive();
        var userRec = msg.getValue();

        assert userRec.age == 28 && userRec.name.equals("Tom");

    }

    /**
     * Publish a new version of the schema
     * But consume the messages with the old schema
     * @throws PulsarClientException
     */
    @Test
    void newVersion1() throws PulsarClientException {
        var topic = topicPrefix + "topic-schema-avro";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var user2 = User2.builder().name("Tom").age(28).address("address1").build();
        var producer2 = pulsarClient.newProducer(Schema.AVRO(User2.class)).topic(topic).create();
        var consumer2 = pulsarClient.newConsumer(Schema.AVRO(User1.class)).topic(topic)
                .subscriptionName("test-schema-sub-avro")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscribe();
        producer2.send(user2);

        ackAllPreviousMesasges(consumer2);
    }

    /**
     * Consume the messages with the new schema
     * @throws PulsarClientException
     */
    @Test
    void newVersion() throws PulsarClientException {
        var topic = topicPrefix + "topic-schema-avro";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();

        var consumer2 = pulsarClient.newConsumer(Schema.AVRO(User2.class)).topic(topic)
                .subscriptionName("test-schema-sub-avro")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscribe();

        ackAllPreviousMesasges(consumer2);
    }

    @Test
    void autoSchema() throws PulsarClientException {

        var topic = topicPrefix + "topic-schema-avro";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer2 = pulsarClient.newProducer(Schema.AVRO(User2.class)).topic(topic).create();
        Consumer<GenericRecord> pulsarConsumer = pulsarClient.newConsumer(Schema.AUTO_CONSUME()).topic(topic).subscriptionName("auto-sub").subscribe();

        producer2.send(User2.builder().name("Tom").age(28).address("address1").build());
        Message<GenericRecord> msg = pulsarConsumer.receive();
        GenericRecord record = msg.getValue();
        System.out.println("schema type:  " + record.getSchemaType());

        record.getFields().forEach((field -> {
            if (field.getName().equals("theNeedFieldName")) {
                Object recordField = record.getField(field);
                //Do some things
            }
        }));
    }
}
