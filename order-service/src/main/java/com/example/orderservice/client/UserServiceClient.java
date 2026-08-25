package com.example.orderservice.client;

import com.example.orderservice.dto.UserResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${user-service.base-url}") String userServiceBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackUser")
    @Bulkhead(name = "userService")
    public UserResponse getUserById(Long userId) {

        try {
            return restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);

        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public UserResponse getSlowUser() {

        return restClient.get()
                .uri("/api/users/slow")
                .retrieve()
                .body(UserResponse.class);
    }

    public UserResponse fallbackUser(Long userId, Throwable ex) {

        throw new RuntimeException(
                "User Service is currently unavailable"
        );
    }

    @Retry(name = "userService")
    public UserResponse getRetryTestUser() {

        return restClient.get()
                .uri("/api/users/retry-test")
                .retrieve()
                .body(UserResponse.class);
    }
}