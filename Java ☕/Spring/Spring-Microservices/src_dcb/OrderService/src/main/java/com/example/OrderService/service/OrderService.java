package com.example.OrderService.service;

import com.example.OrderService.model.request.OrderRequest;
import com.example.OrderService.model.response.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
