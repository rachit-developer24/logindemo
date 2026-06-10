package com.example.logindemo.controller;

import com.example.logindemo.dto.LoginRequest;
import com.example.logindemo.dto.LoginResponse;
import com.example.logindemo.service.AuthService;
import com.example.logindemo.service.CookieService;
import com.example.logindemo.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, CookieService cookieService, JwtService jwtService) {
        this.authService = authService;
        this.cookieService = cookieService;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        if (!response.success()) {
            return ResponseEntity.status(401).body(response);
        }

        // Issue BOTH an access token (short) and a refresh token (long).
        ResponseCookie accessCookie = cookieService.createTokenCookie(response.token());
        ResponseCookie refreshCookie =
                cookieService.createRefreshCookie(jwtService.generateRefreshToken(request.email()));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(true, response.message(), null));
    }

    // Exchange a valid refresh token for a fresh access token - no re-login needed.
    @PostMapping("/api/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        String refreshToken = readCookie(request, "refresh_token");

        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).build();   // missing/expired refresh -> must log in again
        }

        String email = jwtService.extractEmail(refreshToken);
        ResponseCookie newAccessCookie = cookieService.createTokenCookie(jwtService.generateToken(email));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .build();
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Void> logout() {
        // Clear BOTH cookies.
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.clearTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.clearRefreshCookie().toString())
                .build();
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
