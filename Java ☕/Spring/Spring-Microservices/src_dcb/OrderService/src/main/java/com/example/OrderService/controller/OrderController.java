package com.example.OrderService.controller;

import com.example.OrderService.model.request.OrderRequest;
import com.example.OrderService.model.response.OrderResponse;
import com.example.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    @PreAuthorize("hasAuthority('Customer')")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("OrderID: {{}}", orderId);
        return new ResponseEntity<>(orderId, OK);
    }


    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyAuthority('Admin', 'Customer')")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(orderId);
        return new ResponseEntity<>(orderResponse, OK);
    }

}
