package com.example.orderservice.controller;

import com.example.orderservice.kafka.OrderProducer;
import com.example.sharedlib.model.dto.OrderDTO;
import com.example.sharedlib.model.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping("/orders")
    public String placeOrder(@RequestBody OrderDTO order) {
        order.setOrderId(UUID.randomUUID().toString());

        OrderEvent orderEvent = OrderEvent.builder()
                .order(order)
                .statusOfEvent_Order("PENDING")
                .message("order status is in pending state")
                .build();

        orderProducer.sendMessage(orderEvent);

        return "Order placed success...";
    }

}
