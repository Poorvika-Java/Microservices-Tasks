package com.example.orderservice.service;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.CreateOrderResponse;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.exception.DuplicateOrderException;
import com.example.orderservice.exception.InvalidOrderException;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.orderservice.dto.OrderListResponse;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final UserServiceClient userServiceClient;
    private final OrderRepository orderRepository;

    public OrderService(
            UserServiceClient userServiceClient,
            OrderRepository orderRepository) {

        this.userServiceClient = userServiceClient;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CreateOrderResponse createOrder(
            CreateOrderRequest request) {

        log.info(
                "Starting order creation userId={} correlationId={}",
                request != null ? request.getUserId() : null,
                org.slf4j.MDC.get("correlationId")
        );

        if (request == null) {
            throw new InvalidOrderException(
                    "Order request cannot be null"
            );
        }

        if (request.getUserId() == null ||
                request.getUserId() <= 0) {

            throw new InvalidOrderException(
                    "User ID must be greater than 0"
            );
        }

        if (request.getIdempotencyKey() == null ||
                request.getIdempotencyKey().isBlank()) {

            throw new InvalidOrderException(
                    "Idempotency key is required"
            );
        }

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new InvalidOrderException(
                    "Order must contain at least one item"
            );
        }

        if (orderRepository.existsByIdempotencyKey(
                request.getIdempotencyKey())) {

            log.warn(
                    "Duplicate order request detected userId={}",
                    request.getUserId()
            );

            throw new DuplicateOrderException(
                    "Order already exists for idempotency key: "
                            + request.getIdempotencyKey()
            );
        }

        log.info(
                "Calling User Service userId={}",
                request.getUserId()
        );

        UserResponse user =
                userServiceClient.getUserById(
                        request.getUserId()
                );

        if (user == null) {

            log.warn(
                    "User Service returned user not found userId={}",
                    request.getUserId()
            );

            throw new UserNotFoundException(
                    "User " + request.getUserId()
                            + " was not found"
            );
        }

        log.info(
                "User verified successfully userId={}",
                request.getUserId()
        );

        Order order = new Order();

        order.setUserId(request.getUserId());
        order.setStatus("CREATED");
        order.setIdempotencyKey(
                request.getIdempotencyKey()
        );

        for (OrderItemRequest itemRequest :
                request.getItems()) {

            if (itemRequest == null) {
                throw new InvalidOrderException(
                        "Order item cannot be null"
                );
            }

            if (itemRequest.getProductId() == null ||
                    itemRequest.getProductId() <= 0) {

                throw new InvalidOrderException(
                        "Product ID must be greater than 0"
                );
            }

            if (itemRequest.getQuantity() == null ||
                    itemRequest.getQuantity() <= 0) {

                throw new InvalidOrderException(
                        "Quantity must be greater than 0"
                );
            }

            OrderItem item = new OrderItem();

            item.setProductId(
                    itemRequest.getProductId()
            );

            item.setQuantity(
                    itemRequest.getQuantity()
            );

            order.addItem(item);
        }

        Order savedOrder =
                orderRepository.save(order);

        log.info(
                "Order created successfully orderId={} userId={}",
                savedOrder.getId(),
                savedOrder.getUserId()
        );

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long orderId) {

        if (orderId == null || orderId <= 0) {

            throw new InvalidOrderException(
                    "Order ID must be greater than 0"
            );
        }

        Order order =
                orderRepository.findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }

        UserResponse user =
                userServiceClient.getUserById(
                        order.getUserId()
                );

        if (user == null) {

            throw new UserNotFoundException(
                    "User " + order.getUserId()
                            + " was not found"
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                user
        );
    }

    @Transactional(readOnly = true)
    public List<OrderListResponse> getOrdersByUserId(Long userId){

        if (userId == null || userId <= 0) {

            throw new InvalidOrderException(
                    "User ID must be greater than 0"
            );
        }

        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> new OrderListResponse(
                        order.getId(),
                        order.getUserId(),
                        order.getStatus(),
                        order.getIdempotencyKey()
                ))
                .toList();
    }
}
