package com.techprimers.domaincrawler.service;

import com.techprimers.domaincrawler.domain.Domain;
import com.techprimers.domaincrawler.domain.DomainList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainCrawlerService {

    private static final String KAFKA_TOPIC = "web-domains";
    private final KafkaTemplate<String, Domain> kafkaTemplate;

    public void crawl(String name) {
        Mono<DomainList> domainListMono = WebClient.create()
                .get()
                .uri("https://api.domainsdb.info/v1/domains/search?domain=" + name + "&zone=com")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DomainList.class);

        domainListMono.subscribe(
                domainList -> domainList.getDomains()
                        .forEach(
                                domain -> {
                                    kafkaTemplate.send(KAFKA_TOPIC, domain);
                                    log.info("==> Domain msg => {}", domain.getDomain());
                                }
                        )
        );
    }

}
