package com.example.orderservice.service;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final UserServiceClient userServiceClient;

    private final Map<Long, Long> orders = new HashMap<>();

    public OrderService(UserServiceClient userServiceClient) {

        this.userServiceClient = userServiceClient;

        orders.put(101L, 1L);
        orders.put(102L, 2L);
        orders.put(103L, 3L);
    }

    public OrderResponse getOrderById(Long orderId) {

        Long userId = orders.get(orderId);

        if (userId == null) {
            return null;
        }

        UserResponse user = userServiceClient.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found for order: " + orderId);
        }

        return new OrderResponse(orderId,userId,user);
    }
}