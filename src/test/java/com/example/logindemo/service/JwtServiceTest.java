package com.example.logindemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests JwtService WITHOUT Spring - we build it by hand with a literal secret,
// exactly like AuthServiceTest does.
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "test-secret-key-at-least-32-characters-long!!",
                3600000,    // access token lives 1 hour
                10800000);  // refresh token lives 3 hours
    }

    @Test
    void tokenCarriesTheEmailWePutIn() {
        String token = jwtService.generateToken("test@test.com");
        assertEquals("test@test.com", jwtService.extractEmail(token));
    }

    @Test
    void aFreshTokenIsValid() {
        String token = jwtService.generateToken("test@test.com");
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void aGarbageTokenIsNotValid() {
        assertFalse(jwtService.isTokenValid("this.is.not.a.real.token"));
    }

    @Test
    void refreshTokenIsValidAndCarriesTheEmail() {
        String refresh = jwtService.generateRefreshToken("test@test.com");
        assertTrue(jwtService.isTokenValid(refresh));
        assertEquals("test@test.com", jwtService.extractEmail(refresh));
    }
}
