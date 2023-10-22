package com.example.kafkabindersb3.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class CloudStreamConfig {

    // Producer: will send data to topic
    @Bean
    public Supplier<String> producerBinding() {
        return () -> {
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "new data";
        };
    }


    // Processor: will fetch data from one topic, perform its logic and then send new/modified data to the other topic
    @Bean//Function<input, output>
    public Function<String, String> processorBinding() {
        return msg -> msg + " :: " + System.currentTimeMillis();
    }


    // Consumer: will fetch the data from topic
    @Bean
    public Consumer<String> consumerBinding() {
        return msg -> log.info("==> Data Consumed :: {}", msg.toUpperCase());
    }

}
