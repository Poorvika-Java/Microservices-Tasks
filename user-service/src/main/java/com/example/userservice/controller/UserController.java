package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        UserResponse user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/slow")
    public ResponseEntity<UserResponse> slowUser() throws InterruptedException {

        Thread.sleep(10000);

        return ResponseEntity.ok(userService.getUserById(1L));
    }

    @GetMapping("/retry-test")
    public ResponseEntity<UserResponse> retryTest() {

        return ResponseEntity.ok(userService.retryTest());
    }
}