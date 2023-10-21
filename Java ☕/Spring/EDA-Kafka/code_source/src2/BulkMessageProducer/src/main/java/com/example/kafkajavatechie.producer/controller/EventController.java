package com.example.kafkajavatechie.controller;

import com.example.kafkajavatechie.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka/producer")
public class EventController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping
    public ResponseEntity<String> sendMsg(@RequestBody String msg) {
        try {

            // performing build operation; since we're using kafka, this will run in a short
            // amount of time
            for (int i = 0; i < 10_000; i++)
                kafkaProducerService.sendMessage(msg);

            return ResponseEntity.ok("msg sent...");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("...");
        }
    }

}
