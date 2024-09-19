package com.reactivespring.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(-10)
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        var response = exchange.getResponse();
        Mono<Void> monoVoid = null;

        log.error("Exception message is {}", ex.getMessage(), ex);

        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer errorMessage = bufferFactory.wrap(ex.getMessage().getBytes());

        if(ex instanceof ReviewDataException) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            monoVoid = response.writeWith(Mono.just(errorMessage));
        }
        else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            monoVoid = response.writeWith(Mono.just(errorMessage));
        }
        return monoVoid;
    }

}
