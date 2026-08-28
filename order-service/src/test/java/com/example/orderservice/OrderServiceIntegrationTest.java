package com.example.orderservice;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureMockMvc
class OrderServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void contextLoadsSuccessfully() {
    }

    @Test

    @WithMockUser(username = "testuser", roles = "USER")
    void createOrder_success() throws Exception {

        UserResponse user = new UserResponse(101L, "Test User", "test@example.com");

        when(userServiceClient.getUserById(101L))
                .thenReturn(user);

        String request = """
                {
                    "userId": 101,
                    "idempotencyKey": "test-key-001",
                    "items": [
                        {
                            "productId": 501,
                            "quantity": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders").header("Idempotency-Key", "test-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isCreated());
    }

    @Test

    @WithMockUser(username = "testuser", roles = "USER")
    void createOrder_validationFailure() throws Exception {

        String request = """
                {
                    "userId": -1,
                    "items": [
                        {
                            "productId": 501,
                            "quantity": 0
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders").header("Idempotency-Key", "test-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_unauthorizedRequest() throws Exception {

        String request = """
                {
                    "userId": 101,
                    "idempotencyKey": "test-key-001",
                    "items": [
                        {
                            "productId": 501,
                            "quantity": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders").header("Idempotency-Key", "test-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test

    @WithMockUser(username = "testuser", roles = "USER")
    void createOrder_duplicateOrder() throws Exception {
        UserResponse user = new UserResponse(101L, "Test User", "test@example.com");

        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(user);

        String request = """
                {
                    "userId": 101,
                    "idempotencyKey": "test-key-001",
                    "items": [
                        {
                            "productId": 501,
                            "quantity": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders").header("Idempotency-Key", "test-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/orders").header("Idempotency-Key", "test-key-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        )
        .andExpect(status().isConflict());
    }
}


