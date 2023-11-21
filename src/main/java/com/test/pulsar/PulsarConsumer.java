package com.test.pulsar;

import com.test.pulsar.bo.User1;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.listener.AckMode;
import org.springframework.stereotype.Service;

import static com.test.pulsar.Constants.TOPIC;
import static com.test.pulsar.Constants.USER_DEAD_LETTER_TOPIC;

@Service
@Slf4j
public class PulsarConsumer {

    @PulsarListener(
            subscriptionName = "topic-subscription-${custom.pod-name}",
            topics = TOPIC,
            schemaType = SchemaType.AVRO,
            ackMode = AckMode.RECORD,
//            properties = {"ackTimeout=60s"},
            deadLetterPolicy = "deadLetterPolicy",
            subscriptionType = SubscriptionType.Shared
    )
    public void stringTopicListener(User1 user1) {
        log.info("Received String message: {}", user1.name);
    }

    @PulsarListener(
            subscriptionName = "dead-letter-topic-subscription",
            topics = USER_DEAD_LETTER_TOPIC,
            subscriptionType = SubscriptionType.Shared
    )
    public void userDlqTopicListener(User1 user) {
        log.info("Received user object in user-DLQ with email: {}", user.name);
    }
}
