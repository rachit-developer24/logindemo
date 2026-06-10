package com.example.logindemo.dto;

public record LoginResponse(boolean success, String message, String token) {
}
