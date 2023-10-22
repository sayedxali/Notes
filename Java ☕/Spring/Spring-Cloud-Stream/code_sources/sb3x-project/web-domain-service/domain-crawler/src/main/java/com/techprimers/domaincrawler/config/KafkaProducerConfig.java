package com.techprimers.domaincrawler.config;

import com.techprimers.domaincrawler.domain.Domain;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, Domain> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Domain> producerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configs.put(JsonDeserializer.KEY_DEFAULT_TYPE, Domain.class);
        configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Domain.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaProducerFactory<>(configs);
    }

}
