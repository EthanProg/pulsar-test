package com.test.pulsar;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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


}
