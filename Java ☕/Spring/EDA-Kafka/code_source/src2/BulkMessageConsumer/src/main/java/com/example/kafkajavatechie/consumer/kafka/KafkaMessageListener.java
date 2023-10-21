package com.example.kafkajavatechie.consumer.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageListener {

    @KafkaListener(
            topics = "new-topic-javatechie",
            groupId = "bulk-message"
    )
    public void consume(String msg) {
        log.info("consumer1 consumed the msg ==> {}", msg);
    }


    @KafkaListener(
            topics = "new-topic-javatechie",
            groupId = "bulk-message"
    )
    public void consume1(String msg) {
        log.info("consumer2 consumed the msg ==> {}", msg);
    }


    @KafkaListener(
            topics = "new-topic-javatechie",
            groupId = "bulk-message"
    )
    public void consume2(String msg) {
        log.info("consumer3 consumed the msg ==> {}", msg);
    }


    @KafkaListener(
            topics = "new-topic-javatechie",
            groupId = "bulk-message"
    )
    public void backupConsumer(String msg) {
        log.info("backupConsumer consumed the msg ==> {}", msg);
    }

}
