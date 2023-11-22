package com.test.pulsar;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.DefaultPulsarProducerFactory;
import org.springframework.pulsar.core.PulsarProducerFactory;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.pulsar.listener.DefaultPulsarConsumerErrorHandler;
import org.springframework.pulsar.listener.PulsarConsumerErrorHandler;
import org.springframework.pulsar.listener.PulsarDeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;
import java.util.function.BiFunction;

import static com.test.pulsar.Constants.USER_DEAD_LETTER_TOPIC;

@Configuration
@Slf4j
public class PulsarConfigs {

//    @Bean
//    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
//        return schemaResolver -> {
//            schemaResolver.addCustomSchemaMapping(User1.class, Schema.AVRO(User1.class));
//        };
//    }

    @Bean
    DeadLetterPolicy deadLetterPolicy() {
        return DeadLetterPolicy.builder()
                .maxRedeliverCount(10)
                .deadLetterTopic(USER_DEAD_LETTER_TOPIC)
                .build();
    }

    /**
     * If your application only has a single @PulsarListener and a single
     * PulsarListenerConsumerBuilderCustomizer bean registered then the customizer will be automatically applied.
     * @return
     */
//    @Bean
//    ConsumerBuilderCustomizer<User1> myCustomizer() {
//        return builder -> builder.consumerName("myConsumer")
//                .subscriptionName("mySubscription")
//                .subscriptionType(SubscriptionType.Shared);
//    }

//    @Bean
//    @Order(100)
//    ProducerInterceptor firstInterceptor() {
//
//    }


//    @Bean
//    PulsarConsumerErrorHandler<Integer> pulsarConsumerErrorHandler(PulsarClient pulsarClient) {
//        PulsarProducerFactory<Integer> pulsarProducerFactory = new DefaultPulsarProducerFactory<>(pulsarClient, Map.of());
//        PulsarTemplate<Integer> pulsarTemplate = new PulsarTemplate<>(pulsarProducerFactory);
//
//        BiFunction<Consumer<?>, Message<?>, String> destinationResolver =
//                (c, m) -> "my-foo-dlt";
//
//        PulsarDeadLetterPublishingRecoverer<Integer> pulsarDeadLetterPublishingRecoverer =
//                new PulsarDeadLetterPublishingRecoverer<>(pulsarTemplate, destinationResolver);
//
//        return new DefaultPulsarConsumerErrorHandler<>(pulsarDeadLetterPublishingRecoverer,
//                new FixedBackOff(100, 5));
//    }

}
