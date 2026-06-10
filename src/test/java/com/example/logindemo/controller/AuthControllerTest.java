package com.example.logindemo.controller;

import com.example.logindemo.dto.LoginRequest;
import com.example.logindemo.dto.LoginResponse;
import com.example.logindemo.service.AuthService;
import com.example.logindemo.service.CookieService;
import com.example.logindemo.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests the AuthController by building it with real services (no Spring needed).
class AuthControllerTest {

    private AuthController authController;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-at-least-32-characters-long!!", 3600000, 10800000);
        AuthService authService = new AuthService(jwtService);
        CookieService cookieService = new CookieService(false, 3600, 10800);
        authController = new AuthController(authService, cookieService, jwtService);
    }

    @Test
    void loginWithCorrectCredentialsReturns200AndSetsTwoCookies() {
        ResponseEntity<LoginResponse> response =
                authController.login(new LoginRequest("test@test.com", "1234"));
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().success());
        // one Set-Cookie for the access token, one for the refresh token
        assertEquals(2, response.getHeaders().get(HttpHeaders.SET_COOKIE).size());
    }

    @Test
    void loginWithWrongPasswordReturns401() {
        ResponseEntity<LoginResponse> response =
                authController.login(new LoginRequest("test@test.com", "wrong"));
        assertEquals(401, response.getStatusCode().value());
        assertFalse(response.getBody().success());
    }

    @Test
    void refreshWithAValidRefreshTokenReturns200() {
        String refreshToken = jwtService.generateRefreshToken("test@test.com");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refresh_token", refreshToken));

        ResponseEntity<Void> response = authController.refresh(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void refreshWithNoCookieReturns401() {
        ResponseEntity<Void> response = authController.refresh(new MockHttpServletRequest());
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void logoutReturns200AndClearsTwoCookies() {
        ResponseEntity<Void> response = authController.logout();
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getHeaders().get(HttpHeaders.SET_COOKIE).size());
    }
}
