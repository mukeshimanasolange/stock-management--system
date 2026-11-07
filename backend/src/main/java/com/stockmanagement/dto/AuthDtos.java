package com.stockmanagement.dto;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(String fullName, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String token, UserDto user) {}
}
