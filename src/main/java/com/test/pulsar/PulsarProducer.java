package com.test.pulsar;

import com.test.pulsar.bo.User1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.ProducerAccessMode;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.test.pulsar.Constants.TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class PulsarProducer {

    private final PulsarTemplate<User1> pulsarTemplate;

    public void sendMessage(User1 user1) throws PulsarClientException {
        pulsarTemplate.send(TOPIC, user1, Schema.AVRO(User1.class));
    }

    public void sendMessageWithCustomizer(User1 user) throws PulsarClientException {
        pulsarTemplate.newMessage(user).withMessageCustomizer(mc ->
                        mc.deliverAfter(10L, TimeUnit.SECONDS)
                        .sequenceId(1L).key("key")
                        .properties(Map.of("key", "value"))
                ).withProducerCustomizer(pc ->
                        pc.accessMode(ProducerAccessMode.Shared)
                ).send();
    }

}
