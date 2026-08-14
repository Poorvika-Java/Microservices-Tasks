package com.example.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public String userFallback() {
        return "User Service is currently unavailable";
    }

    @GetMapping("/fallback/order")
    public String orderFallback() {
        return "Order Service is currently unavailable";
    }
}