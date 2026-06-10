package com.example.logindemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProfileController {

    @GetMapping("/api/me")
    public Map<String, String> me(Authentication authentication) {
        String email = authentication.getName();
        return Map.of("email", email, "role", "USER");
    }
}
