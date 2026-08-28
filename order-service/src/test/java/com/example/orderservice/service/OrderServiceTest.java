package com.example.orderservice.service;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.InvalidOrderException;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(101L);
        request.setIdempotencyKey("test-key-101");

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(501L);
        item.setQuantity(2);

        request.setItems(java.util.List.of(item));

        UserResponse user = mock(UserResponse.class);

        when(userServiceClient.getUserById(101L))
                .thenReturn(user);

        Order savedOrder = new Order();
        savedOrder.setUserId(101L);
        savedOrder.setStatus("CREATED");

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        var response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(101L, response.getUserId());
        assertEquals("CREATED", response.getStatus());

        verify(userServiceClient).getUserById(101L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_userNotFound() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(101L);
        request.setIdempotencyKey("test-key-101");

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(501L);
        item.setQuantity(2);

        request.setItems(java.util.List.of(item));

        when(userServiceClient.getUserById(101L))
                .thenReturn(null);

        assertThrows(
                UserNotFoundException.class,
                () -> orderService.createOrder(request)
        );

        verify(userServiceClient).getUserById(101L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_invalidOrder() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(101L);
        request.setIdempotencyKey("test-key-101");
        request.setItems(java.util.Collections.emptyList());

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.createOrder(request)
        );

        verifyNoInteractions(userServiceClient);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void createOrder_userServiceFailure() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(101L);
        request.setIdempotencyKey("test-key-101");

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(501L);
        item.setQuantity(2);

        request.setItems(java.util.List.of(item));

        when(userServiceClient.getUserById(101L))
                .thenThrow(new RuntimeException("User Service unavailable"));

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );

        verify(userServiceClient).getUserById(101L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
