package com.example.OrderService.config;

import com.example.OrderService.exceptions.RestResponseEntityExceptionHandler;
import com.example.OrderService.external.decoder.CustomErrorDecoder;
import com.example.OrderService.external.response.ErrorResponse;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>This class is also responsible for the exception occurring!</p>
 * Classes Related:
 * <ul>
 *     <li>{@link FeignConfig}</li>
 *     <li>{@link RestResponseEntityExceptionHandler}</li>
 *     <li>{@link CustomErrorDecoder}</li>
 *     <li>{@link ErrorResponse}</li>
 * </ul>
 */
@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
