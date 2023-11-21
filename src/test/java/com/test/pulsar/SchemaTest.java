package com.test.pulsar;

import com.test.pulsar.bo.User1;
import com.test.pulsar.bo.User;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.junit.jupiter.api.Test;

class SchemaTest extends BasicTest {

    @Test
    void test1() throws PulsarClientException {
        var topic = topicPrefix + "topic-schema";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(JSONSchema.of(User.class)).topic(topic).create();
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

        var user = new User1();
        user.setName("Tom");
        user.setAge(28);
        producer.send(user);

        var msg = consumer.receive();
        var userRec = msg.getValue();

        assert userRec.age == 28 && userRec.name.equals("Tom");
    }

    @Test
    void test() {

    }
}
