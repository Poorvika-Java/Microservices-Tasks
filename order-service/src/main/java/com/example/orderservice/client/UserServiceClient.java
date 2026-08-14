package com.example.orderservice.client;

import com.example.orderservice.dto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder,@Value("${user-service.base-url}") String userServiceBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public UserResponse getUserById(Long userId) {

        try {
            return restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);

        } catch (HttpClientErrorException.NotFound e) {
            return null;

        } catch (Exception e) {
            throw new RuntimeException("User Service is unavailable");
        }
    }
}