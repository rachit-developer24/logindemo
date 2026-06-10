package com.example.logindemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;


@Service
public class CookieService {

    private final boolean secure;
    private final long maxAge;
    private final long refreshMaxAge;

    public CookieService(
            @Value("${cookie.secure}") boolean secure,
            @Value("${cookie.max-age}") long maxAge,
            @Value("${cookie.refresh-max-age}") long refreshMaxAge
    ) {
        this.secure = secure;
        this.maxAge = maxAge;
        this.refreshMaxAge = refreshMaxAge;
    }

    public ResponseCookie createTokenCookie(String token) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }

    // Build an expired cookie (max-age 0) to clear the token on logout.
    public ResponseCookie clearTokenCookie() {
        return ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    // Refresh cookie - longer-lived, scoped to /api/refresh so it is ONLY ever sent there.
    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(secure)
                .path("/api/refresh")
                .maxAge(refreshMaxAge)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/api/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
