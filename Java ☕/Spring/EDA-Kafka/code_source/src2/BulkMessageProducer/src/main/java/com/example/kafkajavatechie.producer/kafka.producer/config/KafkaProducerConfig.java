package com.example.kafkajavatechie.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.topic.name}")
    private String KAFKA_TOPIC_NAME;

    @Bean
    public NewTopic newTopic() {
        return new NewTopic(
                KAFKA_TOPIC_NAME, //topic-name
                3, //partition number
                (short) 1 //replication-factor
        );
    }

}
