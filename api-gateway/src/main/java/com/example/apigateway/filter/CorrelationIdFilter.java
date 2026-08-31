package com.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {

    public static final String CORRELATION_ID = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String finalCorrelationId = correlationId;

        exchange.getResponse()
                .getHeaders()
                .set(CORRELATION_ID, finalCorrelationId);

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID, finalCorrelationId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request)
                .build();

        return chain.filter(mutatedExchange)
                .contextWrite(context ->
                        context.put(CORRELATION_ID, finalCorrelationId));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}