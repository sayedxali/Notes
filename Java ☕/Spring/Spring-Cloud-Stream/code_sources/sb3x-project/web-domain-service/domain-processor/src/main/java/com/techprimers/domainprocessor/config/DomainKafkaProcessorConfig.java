package com.techprimers.domainprocessor.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Slf4j
@Configuration
public class DomainKafkaProcessorConfig {

    @Bean//Function<consume_msg, produce_msg>
    public Function<KStream<String, Domain>, KStream<String, Domain>> domainProcessor() {

        // filter the whole data based on the active or inactive scenario
        return kStream ->
                kStream.filter((key, domain) -> {
                    if (domain.isDead())
                        log.warn("Inactive domain: {}", domain.getDomain());
                    else log.info("Active domain: {}", domain.getDomain());
                    return !domain.isDead();
                });

    }

}
