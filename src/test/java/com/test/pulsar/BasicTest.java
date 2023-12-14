package com.test.pulsar;

import org.apache.pulsar.client.api.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

class BasicTest {

    // protected static final String pulsarUrl = "pulsar://10.14.0.208:30363/";
    // protected static final String topicPrefix = "persistent://test1/test-namespace/";
    protected static final String pulsarUrl = "pulsar://10.14.0.208:6650/";
    protected static final String topicPrefix = "persistent://public/default/";

    @Test
    void test1() throws PulsarClientException {
        var topic = topicPrefix + "topic1";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(Schema.STRING).producerName("producer-1").topic(topic).sendTimeout(0, TimeUnit.SECONDS).create();
        var consumer = pulsarClient.newConsumer(Schema.STRING).topic(topic).subscriptionName("test-subscription").subscribe();

        // ackAllPreviousMesasges(consumer);

        var id = String.valueOf(UUID.randomUUID());

        producer.newMessage()
                .key("key1")
                .property("p1", id)
                .value(id)
                .send();

        long start = System.currentTimeMillis();
        var msg = consumer.receive();
        String consumeId = msg.getValue();
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start));
        consumer.acknowledgeCumulative(msg.getMessageId());

        // Assertions.assertEquals(id, consumeId);
    }

    protected <T> void ackAllPreviousMesasges(Consumer<T> consumer) throws PulsarClientException {
        while (true) {
            Message<T> msg = consumer.receive();
            try {
                // Process the message
                System.out.printf("Message received: %s", msg.getValue());
                // Acknowledge the message cumulatively
                // consumer.acknowledgeCumulative(msg.getMessageId());
            } catch (Exception e) {
                // Handle the exception
            }
        }
    }
}
