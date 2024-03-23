package com.example.OrderService.service;

import com.example.OrderService.exceptions.CustomException;
import com.example.OrderService.external.client.PaymentService;
import com.example.OrderService.external.client.ProductService;
import com.example.OrderService.model.entity.Order;
import com.example.OrderService.model.request.OrderRequest;
import com.example.OrderService.model.request.PaymentRequest;
import com.example.OrderService.model.response.OrderResponse;
import com.example.OrderService.model.response.PaymentResponse;
import com.example.OrderService.model.response.ProductResponse;
import com.example.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        // ORDER-SERVICE -> save the data with status order
        log.info("Placing order request: {{}}", orderRequest);

        // PRODUCT-SERVICE -> Block products (reduce the quantity)
        this.productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status CREATED");
        Order order
                = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .quantity(orderRequest.getQuantity())
                .productId(orderRequest.getProductId())
                .build();

        order = this.orderRepository.save(order);

        // PAYMENT-SERVICE -> Payments (complete, cancelled, etc)
        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getOrderId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus;
        try {

            this.paymentService.doPayment(paymentRequest);
            log.info("Payment Done Successfully. Changing the Order Status to PLACED");
            orderStatus = "PLACED";

        } catch (Exception ex) {
            log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        this.orderRepository.save(order);
        log.info("Order placed successfully with orderID: {}", order.getOrderId());
        return order.getOrderId();
    }


    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for OrderID: {}", orderId);

        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(
                                "Order not found for the orderID: " + orderId,
                                "NOT_FOUND",
                                404
                        )
                );

        log.info("Invoking Product Service to fetch the product for id: {}", order.getProductId());
        ProductResponse productResponse = this.restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );

        log.info("Getting payment information from the PAYMENT-SERVICE");
        PaymentResponse paymentResponse = this.restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .orderId(paymentResponse.getOrderId())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .status(paymentResponse.getStatus())
                .build();

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
    }

}
