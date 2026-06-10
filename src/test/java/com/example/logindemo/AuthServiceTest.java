package com.example.logindemo;

import com.example.logindemo.dto.LoginRequest;
import com.example.logindemo.dto.LoginResponse;
import com.example.logindemo.service.AuthService;
import com.example.logindemo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtService jwtService = new JwtService("test-secret-key-at-least-32-characters-long!!", 3600000, 10800000);
        authService = new AuthService(jwtService);
    }

    @Test
    void loginWithCorrectCredentialsShouldReturnSuccess() {
        LoginRequest request = new LoginRequest("test@test.com", "1234");
        LoginResponse response = authService.login(request);
        assertTrue(response.success());
        assertEquals("Login successful!", response.message());
    }

    @Test
    void loginWithWrongPasswordShouldReturnFailure() {
        LoginRequest request = new LoginRequest("test@test.com", "wrong");
        LoginResponse response = authService.login(request);
        assertFalse(response.success());
        assertEquals("Invalid email or password", response.message());
    }

    @Test
    void loginWithWrongEmailShouldReturnFailure() {
        LoginRequest request = new LoginRequest("wrong@test.com", "1234");
        LoginResponse response = authService.login(request);
        assertFalse(response.success());
        assertEquals("Invalid email or password", response.message());
    }
}
