package com.example.OrderService.integration;

import com.example.OrderService.config.OrderServiceTestConfig;
import com.example.OrderService.model.entity.Order;
import com.example.OrderService.model.enums.PaymentMode;
import com.example.OrderService.model.request.OrderRequest;
import com.example.OrderService.repository.OrderRepository;
import com.example.OrderService.service.OrderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"}) // random port
@EnableConfigurationProperties // to use application.yml-test file
@AutoConfigureMockMvc // calling the api itself
@ContextConfiguration(classes = {OrderServiceTestConfig.class}) // to call the configuration in the test (for service-registry configs that what instances of services are attached to it)
class OrderControllerTest {

    private @Autowired OrderService orderService;
    private @Autowired OrderRepository orderRepository;
    private @Autowired MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer
            = WireMockExtension.newInstance()
            .options(
                    WireMockConfiguration
                            .wireMockConfig()
                            .port(8080)
            ).build();

    private @Autowired ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        wireMockServer.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(
                        aResponse()
                                .withStatus(OK.value())
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                ));
    }

    private void getPaymentDetails() throws IOException {
        wireMockServer.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(
                        aResponse()
                                .withStatus(OK.value())
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withBody(copyToString(
                                        OrderControllerTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("mock/GetPaymentMock.json"),
                                        Charset.defaultCharset()
                                ))
                )
        );
    }

    private void doPayment() {
        wireMockServer.stubFor(
                post(urlEqualTo("/payment"))
                        .willReturn(aResponse()
                                .withStatus(OK.value())
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        )
        );
    }

    private void getProductDetailsResponse() throws IOException {
        // GET /product/{productId}
        wireMockServer.stubFor(get("/product/1")
                .willReturn(
                        aResponse()
                                .withStatus(OK.value())
                                .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                                .withBody(copyToString(
                                        OrderControllerTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("mock/GetProductMock.json"),
                                        Charset.defaultCharset()
                                ))
                )
        );
    }

    @Test
    public void test_When_PlaceOrder_And_DoPayment_Success() throws Exception {
        // First place the order
        // Get order by orderId from db and check
        // Check output

        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/order/placeOrder")
                                .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(this.objectMapper.writeValueAsBytes(orderRequest))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();
        Optional<Order> order = this.orderRepository.findById(Long.valueOf(orderId));
        assertTrue(order.isPresent());

        Order o = order.get();
        assertEquals(Long.parseLong(orderId), o.getOrderId(), "IDs should be same!");
        assertEquals(orderRequest.getTotalAmount(), o.getAmount(), "Amount  should be same!");
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .totalAmount(200)
                .build();
    }

}



