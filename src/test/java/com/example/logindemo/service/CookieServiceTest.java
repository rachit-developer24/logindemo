package com.example.logindemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests that CookieService builds cookies with the right security settings.
class CookieServiceTest {

    private CookieService cookieService;

    @BeforeEach
    void setUp() {
        // secure=false (local dev), access maxAge 3600s, refresh maxAge 10800s
        cookieService = new CookieService(false, 3600, 10800);
    }

    @Test
    void accessCookieHasCorrectSettings() {
        ResponseCookie cookie = cookieService.createTokenCookie("my-token");
        assertEquals("token", cookie.getName());
        assertEquals("my-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge().getSeconds());
        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void clearingAccessCookieExpiresItImmediately() {
        ResponseCookie cookie = cookieService.clearTokenCookie();
        assertEquals("token", cookie.getName());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    void refreshCookieIsScopedToTheRefreshPath() {
        ResponseCookie cookie = cookieService.createRefreshCookie("refresh-token");
        assertEquals("refresh_token", cookie.getName());
        assertEquals("/api/refresh", cookie.getPath());
        assertEquals(10800, cookie.getMaxAge().getSeconds());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void clearingRefreshCookieExpiresItImmediately() {
        ResponseCookie cookie = cookieService.clearRefreshCookie();
        assertEquals("refresh_token", cookie.getName());
        assertEquals("/api/refresh", cookie.getPath());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }
}
