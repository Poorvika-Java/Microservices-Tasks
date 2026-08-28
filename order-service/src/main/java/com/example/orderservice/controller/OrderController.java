package com.example.orderservice.controller;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final UserServiceClient userServiceClient;

    public OrderController(
            OrderService orderService,
            UserServiceClient userServiceClient) {

        this.orderService = orderService;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/v1/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {

        CreateOrderResponse response = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {

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

    @GetMapping("/orders/slow-user")
    public ResponseEntity<?> slowUserTest() {

        try {

            return ResponseEntity.ok(userServiceClient.getSlowUser());

        } catch (Exception ex) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("User Service is currently unavailable");
        }
    }

    @GetMapping("/orders/retry-test")
    public ResponseEntity<?> retryTest() {

        try {

            UserResponse user = userServiceClient.getRetryTestUser();

            return ResponseEntity.ok(user);

        } catch (Exception ex) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Retry attempts failed");
        }
    }
    @GetMapping("/v1/orders")
    public ResponseEntity<?> getOrdersByUserId(
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                orderService.getOrdersByUserId(userId)
        );
    }
}