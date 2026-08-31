package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        log.info(
                "Received user lookup request for userId={}",
                id
        );

        if (id <= 0) {
            log.warn(
                    "Invalid userId received: {}",
                    id
            );

            return ResponseEntity.badRequest().build();
        }

        UserResponse user = userService.getUserById(id);

        if (user == null) {

            log.warn(
                    "User not found for userId={}",
                    id
            );

            return ResponseEntity.notFound().build();
        }

        log.info(
                "User successfully verified userId={}",
                id
        );

        return ResponseEntity.ok(user);
    }

    @GetMapping("/slow")
    public ResponseEntity<UserResponse> slowUser()
            throws InterruptedException {

        log.warn(
                "Slow User Service endpoint invoked"
        );

        Thread.sleep(10000);

        return ResponseEntity.ok(
                userService.getUserById(1L)
        );
    }

    @GetMapping("/retry-test")
    public ResponseEntity<UserResponse> retryTest() {

        log.warn(
                "Retry test endpoint invoked"
        );

        return ResponseEntity.ok(
                userService.retryTest()
        );
    }
}
