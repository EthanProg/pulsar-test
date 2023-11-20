package com.test.pulsar;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class MultiSubs extends BasicTest {
    @Test
    void test1() throws PulsarClientException {
        var topics = Stream.of(new String[]{"account-topic1", "account-topic2"}).map(a -> topicPrefix + a).toList();

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();

        topics.forEach(a -> {
            try {
                pulsarClient.newProducer(Schema.STRING).topic(a).create()
                        .newMessage()
                        .key("k1")
                        .property("p1", "v1")
                        .value(a)
                        .send();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Send messages to topics: " + topics);

        // Subscribe to a subsets of topics in a namespace, based on regex
        // When subscribing to multiple topics by regex, all topics must be in the same namespace.
        // No ordering guarantees across multiple topics When a producer sends messages to a single topic,
        // all messages are guaranteed to be read from that topic in the same order. However,
        // these guarantees do not hold across multiple topics. So when a producer sends messages to multiple topics,
        // the order in which messages are read from those topics is not guaranteed to be the same.

        // Subscribe to all topics in a namespace
//        Pattern allTopicsInNamespace = Pattern.compile("persistent://public/default/.*");
//        Consumer<byte[]> allTopicsConsumer = pulsarClient.newConsumer()
//                .topicsPattern(allTopicsInNamespace)
//                .subscriptionName("subscription-1")
//                .subscribe();
        var someTopicsInNamespace = Pattern.compile(topicPrefix + ".*");

        var consumer = pulsarClient.newConsumer(Schema.STRING)
                .topicsPattern(someTopicsInNamespace)
                .subscriptionName("subscription-1")
                .subscribe();


        ackMesasges(consumer);
    }
}
