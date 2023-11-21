package com.test.pulsar;

import org.apache.pulsar.client.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Create the nodeport:
 * <snippet>
 * apiVersion: v1
 * kind: Service
 * metadata:
 *   name: pulsar-broker-nodeport
 *   namespace: lfp-pulsar
 * spec:
 *   ports:
 *   - name: pulsar-nodeport
 *     nodePort: 30363
 *     port: 6651
 *     protocol: TCP
 *     targetPort: 6650
 *   - name: http
 *     nodePort: 30207
 *     port: 8081
 *     protocol: TCP
 *     targetPort: 8080
 *   selector:
 *     lfp.app.name: pulsar-standalone
 *   type: NodePort
 * </snippet>
 *
 * Create a topic:
 * <snippet>
 *     curl -sL -X PUT "http://10.14.0.208:30207/admin/v2/tenants/test1" -H "Content-Type: application/json" --data-raw  "{\"allowedClusters\": [\"standalone\"]}"
 * curl -sL -X PUT "http://10.14.0.208:30207/admin/v2/namespaces/test1/test-namespace" -H "Content-Type: application/json" --data-raw "{
 *     \"retention_policies\": {\"retentionTimeInMinutes\": 1440, \"retentionSizeInMB\": 1024},
 *     \"subscription_expiration_time_minutes\": 43200,
 *     \"message_ttl_in_seconds\": 86400
 *   }"
 * </snippet>
 *
 */
class BasicTest {

    protected static final String pulsarUrl = "pulsar://10.14.0.208:30363/";
    protected static final String topicPrefix = "persistent://test1/test-namespace/";

    @Test
    void test1() throws PulsarClientException {
        var topic = topicPrefix + "topic1";

        var pulsarClient = PulsarClient.builder().serviceUrl(pulsarUrl).build();
        var producer = pulsarClient.newProducer(Schema.STRING).topic(topic).create();
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

    protected void ackAllPreviousMesasges(Consumer<String> consumer) throws PulsarClientException {
        while (true) {
            Message<String> msg = consumer.receive();
            try {
                // Process the message
                System.out.printf("Message received: %s", msg.getValue());
                // Acknowledge the message cumulatively
                consumer.acknowledgeCumulative(msg.getMessageId());
            } catch (Exception e) {
                // Handle the exception
            }
        }
    }
}
