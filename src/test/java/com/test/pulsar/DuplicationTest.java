package com.test.pulsar;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Idempotent producer
 * https://streamnative.io/blog/exactly-once-semantics-transactions-pulsar
 */
class DuplicationTest extends BasicTest {

    static Stream<Arguments> topics() {
        return Stream.of(
//                arguments("duplication-topic-1", "Duplicate message detected"),
                arguments("deduplication-topic-1", "Message is not duplicate"));
    }

    @ParameterizedTest
    @MethodSource("topics")
    void testDuplication(String topic, String info) throws PulsarClientException {
        System.out.println("--------------------" + info + "--------------------");

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(Schema.STRING).producerName("producer-1").topic(topic).sendTimeout(0, TimeUnit.SECONDS).create();
        var consumer = pulsarClient.newConsumer(Schema.STRING).topic(topic).subscriptionName("test-duplication").subscribe();
        var id = String.valueOf(UUID.randomUUID());

        try {
            producer.newMessage()
//                    .key("key1")
//                    .property("p1", id)
                    .value(id)
                    .send();
            this.throwExcep();
        } catch (Exception e) {
            producer.newMessage()
//                    .key("key1")
//                    .property("p1", id)
                    .value(id)
                    .send();
        }

        for (int i = 0; i < 2; i++) {
            var msg = consumer.receive();
            String consumeId = msg.getValue();
            System.out.println("value: " + msg.getValue());
            consumer.acknowledgeCumulative(msg.getMessageId());
        }
        System.out.println("--------------------" + info + "--------------------");
    }

    void throwExcep() {
        throw new RuntimeException("test");
    }
}
