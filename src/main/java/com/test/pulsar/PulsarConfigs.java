package com.test.pulsar;

import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.test.pulsar.Constants.USER_DEAD_LETTER_TOPIC;

@Configuration
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
}
