package com.example.orderservice.controller;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserServiceClient userServiceClient;

    public OrderController(
            OrderService orderService,
            UserServiceClient userServiceClient) {

        this.orderService = orderService;
        this.userServiceClient = userServiceClient;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("Order ID must be greater than 0");
        }

        OrderResponse order = orderService.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    @GetMapping("/slow-user")
    public ResponseEntity<?> slowUserTest() {

        try {
            return ResponseEntity.ok(
                    userServiceClient.getSlowUser()
            );

        } catch (Exception ex) {

            return ResponseEntity
                    .status(503)
                    .body("User Service is currently unavailable");
        }
    }

    @GetMapping("/retry-test")
    public ResponseEntity<?> retryTest() {

        try {

            UserResponse user =
                    userServiceClient.getRetryTestUser();

            return ResponseEntity.ok(user);

        } catch (Exception ex) {

            return ResponseEntity
                    .status(503)
                    .body("Retry attempts failed");
        }
    }
}