package com.example.OrderService.exceptions;

import com.example.OrderService.config.FeignConfig;
import com.example.OrderService.external.decoder.CustomErrorDecoder;
import com.example.OrderService.external.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Classes Related:
 * <ul>
 *     <li>{@link FeignConfig}</li>
 *     <li>{@link RestResponseEntityExceptionHandler}</li>
 *     <li>{@link CustomErrorDecoder}</li>
 *     <li>{@link ErrorResponse}</li>
 * </ul>
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(CustomException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), e.getErrorCode()),
                HttpStatus.valueOf(e.getStatus())
        );
    }

}
