package com.example.orderservice.client;

import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.exception.UserServiceUnavailableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);

    private static final String CORRELATION_ID = "X-Correlation-ID";

    private final RestClient restClient;

    public UserServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Retry(name = "userService", fallbackMethod = "fallbackUser")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    @Bulkhead(name = "userService", fallbackMethod = "fallbackUser")
    public UserResponse getUserById(Long userId) {

        try {
            return restClient.get()
                    .uri("http://user-service/api/users/{id}", userId)
                    .header(CORRELATION_ID, correlationId())
                    .retrieve()
                    .body(UserResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {

            return null;
        }
    }

    public UserResponse fallbackUser(Long userId, Throwable ex) {

        log.warn("User Service unavailable userId={} cause={}",
                userId,
                ex.getClass().getSimpleName());

        throw new UserServiceUnavailableException("User Service is currently unavailable");
    }

    public UserResponse getSlowUser() {

        try {

            return restClient.get()
                    .uri("http://user-service/api/users/slow")
                    .header(CORRELATION_ID, correlationId())
                    .retrieve()
                    .body(UserResponse.class);

        } catch (RuntimeException ex) {

            throw new UserServiceUnavailableException("User Service is currently unavailable");
        }
    }

    public UserResponse getRetryTestUser() {

        try {

            return restClient.get()
                    .uri("http://user-service/api/users/retry-test")
                    .header(CORRELATION_ID, correlationId())
                    .retrieve()
                    .body(UserResponse.class);

        } catch (RuntimeException ex) {

            throw new UserServiceUnavailableException("User Service is currently unavailable");
        }
    }

    private String correlationId() {

        String id = MDC.get("correlationId");

        return id == null ? "" : id;
    }
}