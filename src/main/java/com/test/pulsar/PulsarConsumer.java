package com.test.pulsar;

import com.test.pulsar.bo.User1;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.config.PulsarListenerEndpointRegistry;
import org.springframework.pulsar.listener.AckMode;
import org.springframework.pulsar.listener.PulsarMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.test.pulsar.Constants.TOPIC;
import static com.test.pulsar.Constants.USER_DEAD_LETTER_TOPIC;

@Service
@Slf4j
public class PulsarConsumer {

    private PulsarListenerEndpointRegistry registry;

    @PulsarListener(
            // In this most basic form, when the subscriptionName is not provided on the @PulsarListener annotation an
            // auto-generated subscription name will be used. Likewise, when the topics are not directly provided,
            // a topic resolution process is used to determine the destination topic.
            subscriptionName = "topic-subscription-${custom.pod-name}",
            topics = TOPIC,
            schemaType = SchemaType.AVRO,
            ackMode = AckMode.RECORD,
//            properties = {"ackTimeout=60s"},
//            properties = { "subscriptionName=subscription-1", "topicNames=foo-1", "receiverQueueSize=5000","negativeAckRedeliveryDelay=10ms" },
            deadLetterPolicy = "deadLetterPolicy",
//            concurrency = "3",
            subscriptionType = SubscriptionType.Shared
    )
    public void topicListener(User1 user1) {
        log.info("Received message: {}", user1.name);
    }

    void restartPulsarContainer() {
        // The id parameter passed to getListenerContainer is the container id -
        // which will be the value of the @PulsarListener id attribute when pausing/resuming a @PulsarListener.
        PulsarMessageListenerContainer container = registry.getListenerContainer("my-listener-id");
        container.pause();
        container.stop();
        container.start();
    }



//    // Consume in batch mode
//    //  when the entire batch is processed successfully, the entire batch is acknowledged.
//    //  If any records throw an exception, the entire batch is negatively acknowledged.
//    @PulsarListener(
//            // In this most basic form, when the subscriptionName is not provided on the @PulsarListener annotation an
//            // auto-generated subscription name will be used. Likewise, when the topics are not directly provided,
//            // a topic resolution process is used to determine the destination topic.
//            subscriptionName = "topic-subscription-${custom.pod-name}",
//            topics = TOPIC,
//            schemaType = SchemaType.AVRO,
//            ackMode = AckMode.MANUAL,
////            properties = {"ackTimeout=60s"},
////            properties = { "subscriptionName=subscription-1", "topicNames=foo-1", "receiverQueueSize=5000" },
//            deadLetterPolicy = "deadLetterPolicy",
//            batch = true,
//            subscriptionType = SubscriptionType.Shared
//    )
//    public void topicListener(List<User1> user1s) {
//        log.info("Received message size: {}", user1s.size());
//        user1s.forEach(user1 -> log.info("Received message: {}", user1.name));
//    }

    @PulsarListener(
            subscriptionName = "dead-letter-topic-subscription",
            topics = USER_DEAD_LETTER_TOPIC,
            subscriptionType = SubscriptionType.Shared
    )
    public void userDlqTopicListener(User1 user) {
        log.info("Received user object in user-DLQ with email: {}", user.name);
    }

    // MANUAL ACK MODE
//    @PulsarListener(subscriptionName = "hello-pulsar-subscription", topics = "hello-pulsar", ackMode = AckMode.MANUAL)
//    public void listen(Message<String> message, Acknowledgment acknowledgment) {
//        System.out.println("Message Received: " + message.getValue());
//        acknowledgment.acknowledge();
//    }

        // MANUAL ACK MODE WITH BATCH
//    @PulsarListener(subscriptionName = "hello-pulsar-subscription", topics = "hello-pulsar")
//    public void listen(List<Message<String>> messgaes, Acknowlegement acknowledgment) {
//        for (Message<String> message : messages) {
//            try {
//			...
//                acknowledgment.acknowledge(message.getMessageId());
//            }
//            catch (Exception e) {
//                acknowledgment.nack(message.getMessageId());
//            }
//        }
//    }

    // Consume the Pulsar message directly
//    @PulsarListener(subscriptionName = "my-subscription", topics = "my-topic")
//    public void listen(org.apache.pulsar.client.api.Message<String> message) {
//        log.info(message.getValue());
//    }

    /**
     * Sometimes, you need direct access to the Pulsar Consumer object. The following example shows how to get it:
     * @param message
     * @param consumer
     */
//    @PulsarListener(subscriptionName = "hello-pulsar-subscription", topics = "hello-pulsar")
//    public void listen(String message, org.apache.pulsar.client.api.Consumer<String> consumer) {
//        System.out.println("Message Received: " + message);
//        ConsumerStats stats = consumer.getStats();
//    }


//    /**
//     * Customize the consumer
//     * @param message
//     */
//    @PulsarListener(topics = TOPIC, schemaType = SchemaType.AVRO, consumerCustomizer = "myCustomizer")
//    public void listen(String message) {
//        log.info("Message Customized Received: " + message);
//    }


}
