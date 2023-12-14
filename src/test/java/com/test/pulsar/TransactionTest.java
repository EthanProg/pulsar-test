package com.test.pulsar;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.api.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

class TransactionTest {

    protected static final String pulsarUrl = "pulsar://10.14.0.208:6650/";
    protected static final String topicPrefix = "persistent://public/default/";

    //
    @Test
    void testSuccess() throws PulsarClientException {

        var client = PulsarClient.builder()
                // Step 3: create a Pulsar client and enable transactions.
                .enableTransaction(true)
                .serviceUrl(pulsarUrl)
                .build();

        var topic = topicPrefix + "my-topic";
        var topic1 = topicPrefix + "my-topic-1";

        // Pulsar producer
        var producerBuilder = client.newProducer(Schema.STRING);
        var outputProducerOne = producerBuilder.topic(topic)
                .sendTimeout(0, TimeUnit.SECONDS).create();
        var outputProducerTwo = producerBuilder.topic(topic1)
                .sendTimeout(0, TimeUnit.SECONDS).create();

        // Pulsar consumer
        var outputConsumerOne = client.newConsumer(Schema.STRING)
                .subscriptionName("your-subscription-name1").topic(topic).subscribe();
        Consumer<String> outputConsumerTwo = client.newConsumer(Schema.STRING)
                .subscriptionName("your-subscription-name2").topic(topic1).subscribe();

        int count = 2;

        // The transaction timeout is specified as 10 seconds.
        // If the transaction is not committed within 10 seconds, the transaction is automatically aborted.
        Transaction txn = null;
        try {
            txn = client.newTransaction()
                    .withTransactionTimeout(10, TimeUnit.SECONDS).build().get();

            // Send pulsar messages
            outputProducerOne.newMessage(txn).value("Hello Pulsar! outputTopicOne").send();
            outputProducerTwo.newMessage(txn).value("Hello Pulsar! outputTopicTwo").send();

            // Step 8: commit transactions.
            txn.commit().get();
        } catch (ExecutionException | InterruptedException e) {
            if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                e.printStackTrace();
            }

            // If a new transaction is created,
            // then the old transaction should be aborted.
            if (txn != null) {
                txn.abort();
            }
        }

        // Final result: consume messages from output topics and print them.
        Message<String> message = outputConsumerOne.receive();
        System.out.println("Receive transaction message: " + message.getValue());

        message = outputConsumerTwo.receive();
        System.out.println("Receive transaction message: " + message.getValue());
    }

    @Test
    void testFail() throws PulsarClientException {

        var client = PulsarClient.builder()
                // Step 3: create a Pulsar client and enable transactions.
                .enableTransaction(true)
                .serviceUrl(pulsarUrl)
                .build();

        var topic = topicPrefix + "my-topic-2";
        var topic1 = topicPrefix + "my-topic-3";

        // Pulsar producer
        var producerBuilder = client.newProducer(Schema.STRING);
        var outputProducerOne = producerBuilder.topic(topic)
                .sendTimeout(0, TimeUnit.SECONDS).create();
        var outputProducerTwo = producerBuilder.topic(topic1)
                .sendTimeout(0, TimeUnit.SECONDS).create();

        // Pulsar consumer
        var outputConsumerOne = client.newConsumer(Schema.STRING)
                .subscriptionName("your-subscription-name1").topic(topic).subscribe();
        Consumer<String> outputConsumerTwo = client.newConsumer(Schema.STRING)
                .subscriptionName("your-subscription-name2").topic(topic1).subscribe();

        int count = 2;

        // The transaction timeout is specified as 10 seconds.
        // If the transaction is not committed within 10 seconds, the transaction is automatically aborted.
        Transaction txn = null;
        try {
            txn = client.newTransaction()
                    .withTransactionTimeout(10, TimeUnit.SECONDS).build().get();

            // Send pulsar messages
            outputProducerOne.newMessage(txn).value("Hello Pulsar! outputTopicOne").send();
            outputProducerTwo.newMessage(txn).value("Hello Pulsar! outputTopicTwo").send();

            this.throwException();

            // Step 8: commit transactions.
            txn.commit().get();
        } catch (Exception e) {
            if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                e.printStackTrace();
            }

            // If a new transaction is created,
            // then the old transaction should be aborted.
            if (txn != null) {
                txn.abort();
            }
        }

        // Final result: consume messages from output topics and print them.
        Message<String> message = outputConsumerOne.receive();
        System.out.println("Receive transaction message: " + message.getValue());

        message = outputConsumerTwo.receive();
        System.out.println("Receive transaction message: " + message.getValue());
    }
    void throwException() throws Exception {
        throw new Exception("test");
    }
}

