package com.example.OrderService.external.decoder;

import com.example.OrderService.config.FeignConfig;
import com.example.OrderService.exceptions.CustomException;
import com.example.OrderService.exceptions.RestResponseEntityExceptionHandler;
import com.example.OrderService.external.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * <p>This class is responsible for:</p>
 * <p>When the "<code>/placeOrder</code>" resource is hit, and if the quantity of product was not sufficient in the db, then we'll get exception,
 * but, we won't be able to see it in the client, this class is responsible for that specifically.</p>
 * Classes Related:
 * <ul>
 *     <li>{@link FeignConfig}</li>
 *     <li>{@link RestResponseEntityExceptionHandler}</li>
 *     <li>{@link CustomErrorDecoder}</li>
 *     <li>{@link ErrorResponse}</li>
 * </ul>
 */
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("{{}}", response.request().url());
        log.info("{{}}", response.request().headers());

        try {

            ErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
            return new CustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode(), response.status());

        } catch (IOException e) {
            throw new CustomException(
                    "Internal Server Error",
                    "INTERNAL_SERVER_ERROR",
                    500
            );
        }
    }

}
