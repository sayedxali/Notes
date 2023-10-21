package com.example.kafkajavatechie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${spring.kafka.topic.name}")
    private String KAFKA_TOPIC_NAME;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String msg) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KAFKA_TOPIC_NAME, msg);
        future.whenComplete((stringObjectSendResult, throwable) -> {
            if (throwable == null)
                log.info(
                        "msg [{}], offset [{}], partition[{}]",
                        msg, stringObjectSendResult.getRecordMetadata().offset(), stringObjectSendResult.getRecordMetadata().partition()
                );
            else log.error("unable to send msg: cause - {}", throwable.getMessage());
        });
    }

}
