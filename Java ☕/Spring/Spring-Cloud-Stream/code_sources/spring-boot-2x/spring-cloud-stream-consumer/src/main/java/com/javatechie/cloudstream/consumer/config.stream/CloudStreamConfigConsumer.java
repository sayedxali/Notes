package com.javatechie.cloudstream.consumer.config.stream;

import com.javatechie.cloudstream.consumer.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableBinding(Sink.class)
public class CloudStreamConfigConsumer {

    @StreamListener("input")
    public void consumeMessage(Book book) {
        log.info("consume paylod => {}", book);
    }

}
