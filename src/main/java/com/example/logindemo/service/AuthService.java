package com.example.logindemo.service;

import com.example.logindemo.dto.LoginRequest;
import com.example.logindemo.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        if (request.email().equals("test@test.com") && request.password().equals("1234")) {
            String token = jwtService.generateToken(request.email());
            return new LoginResponse(true, "Login successful!", token);
        } else {
            return new LoginResponse(false, "Invalid email or password", null);
        }
    }

}