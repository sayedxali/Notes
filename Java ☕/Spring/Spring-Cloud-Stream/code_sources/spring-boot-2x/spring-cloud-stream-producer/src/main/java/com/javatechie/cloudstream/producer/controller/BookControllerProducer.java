package com.javatechie.cloudstream.producer.controller;

import com.javatechie.cloudstream.producer.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookControllerProducer {

    // send the msg over the network
    private final MessageChannel output; //💡 somehow we'll get exception if we name it `messageChannel`

    @PostMapping("/publish")
    public Book publishEvent(@RequestBody Book book) {
        output.send(MessageBuilder.withPayload(book).build());
        return book;
    }

}
