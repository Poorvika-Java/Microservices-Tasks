package com.example.orderservice.dto;

public class OrderListResponse {

    private Long orderId;
    private Long userId;
    private String status;
    private String idempotencyKey;

    public OrderListResponse(
            Long orderId,
            Long userId,
            String status,
            String idempotencyKey) {

        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}