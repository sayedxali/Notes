package com.example.OrderService.service;

import com.example.OrderService.exceptions.CustomException;
import com.example.OrderService.external.client.PaymentService;
import com.example.OrderService.external.client.ProductService;
import com.example.OrderService.model.entity.Order;
import com.example.OrderService.model.enums.PaymentMode;
import com.example.OrderService.model.request.OrderRequest;
import com.example.OrderService.model.request.PaymentRequest;
import com.example.OrderService.model.response.OrderResponse;
import com.example.OrderService.model.response.PaymentResponse;
import com.example.OrderService.model.response.ProductResponse;
import com.example.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private @InjectMocks OrderServiceImpl orderService;

    private @Mock OrderRepository orderRepository;
    private @Mock ProductService productService;
    private @Mock PaymentService paymentService;
    private @Mock RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Get Order - Success")
    void test_When_Order_Success() {
        // Arrange - Mocking
        Order order = getMockOrder();
        when(this.orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(this.restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class)
        ).thenReturn(getMockProductResponse());

        when(this.restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                PaymentResponse.class)
        ).thenReturn(getMockPaymentResponse());

        // Act - Actual
        OrderResponse orderResponse = this.orderService.getOrderDetails(1);

        // Verification
        verify(this.orderRepository, times(1))
                .findById(anyLong());
        verify(this.restTemplate, times(1))
                .getForObject(
                        "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                        ProductResponse.class
                );
        verify(this.restTemplate, times(1))
                .getForObject(
                        "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                        PaymentResponse.class
                );

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getOrderId(), orderResponse.getOrderId(), "The IDs of the orders should be the same!");
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .orderId(1)
                .amount(100)
                .amount(200)
                .productId(2)
                .build();
    }


    @Test
    @DisplayName("Get Orders - Failure")
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {
        // Arrange - Mocking
        when(this.orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Act - Actual
        CustomException customException = assertThrows(
                CustomException.class,
                () -> this.orderService.getOrderDetails(1)
        );

        // Verification
        verify(this.orderRepository, times(1))
                .findById(anyLong());

        // Assert
        assertEquals("NOT_FOUND", customException.getErrorCode(), "Should throw exception and not find any order!");
        assertEquals(404, customException.getStatus(), "The status should be not found : 404");
    }


    @DisplayName("Place Order - Success")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(this.orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(this.productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(OK));
        when(this.paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L, OK));

        long orderId = this.orderService.placeOrder(orderRequest);

        verify(this.orderRepository, times(2))
                .save(any());
        verify(this.productService, times(1))
                .reduceQuantity(anyLong(), anyLong());
        verify(this.paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(), orderId, "The same ID must return.");

    }


    @DisplayName("Place Order - Payment Fail")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(this.orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(this.productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(OK));
        when(this.paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = this.orderService.placeOrder(orderRequest);

        verify(this.orderRepository, times(2))
                .save(any());
        verify(this.productService, times(1))
                .reduceQuantity(anyLong(), anyLong());
        verify(this.paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(), orderId, "The same ID must return.");
    }


    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

}