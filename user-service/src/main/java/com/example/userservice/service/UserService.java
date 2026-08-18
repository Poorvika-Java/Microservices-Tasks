package com.example.userservice.service;

import com.example.userservice.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private final Map<Long, UserResponse> users = new HashMap<>();

    private final AtomicInteger retryAttempt = new AtomicInteger(0);

    public UserService() {

        users.put(1L, new UserResponse(1L, "Poorvika", "poorvika@example.com"));

        users.put(2L, new UserResponse(2L, "Yash", "yash@example.com"));

        users.put(3L, new UserResponse(3L, "Preethi", "preethi@example.com"));
    }

    public UserResponse getUserById(Long id) {
        return users.get(id);
    }


    public UserResponse retryTest() {

        int attempt = retryAttempt.incrementAndGet();

        if (attempt == 1) {
            throw new RuntimeException("Temporary downstream failure - retry expected");
        }

        return users.get(1L);
    }
}