package com.example.orderservice.dto;

public class OrderResponse {

    private Long orderId;
    private Long userId;
    private UserResponse user;

    public OrderResponse(Long orderId, Long userId, UserResponse user) {
        this.orderId = orderId;
        this.userId = userId;
        this.user = user;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public UserResponse getUser() {
        return user;
    }
}