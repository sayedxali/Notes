package com.techprimers.domainservice.config;

import com.techprimers.domainservice.domain.Domain;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class DomainKafkaConsumerConfig {

    @Bean
    public Consumer<KStream<String, Domain>> domainService() {
        return kStream -> kStream.foreach(
                (key, domain) ->
                        log.info("Domain consumed [{}], Status [{}]", domain.getDomain(), domain.isDead())
        );
    }

}
