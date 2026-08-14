package com.example.userservice.service;

import com.example.userservice.dto.UserResponse;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Long, UserResponse> users = new HashMap<>();

    public UserService() {
        users.put(1L, new UserResponse(1L,"Poorvika","poorvika@example.com"));

        users.put(2L, new UserResponse(2L,"Yash","yash@example.com"));

        users.put(3L, new UserResponse(3L,"Preethi","preethi@example.com"));
    }

    public UserResponse getUserById(Long id) {
        return users.get(id);
    }
}