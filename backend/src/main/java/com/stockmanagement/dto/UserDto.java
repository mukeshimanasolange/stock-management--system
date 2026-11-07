package com.stockmanagement.dto;

import com.stockmanagement.entity.AppUser;

import java.time.Instant;
import java.util.Set;

public record UserDto(Long id, String fullName, String email, Set<String> roles, boolean active, Instant createdAt) {
    public static UserDto from(AppUser user) {
        return new UserDto(user.getId(), user.getFullName(), user.getEmail(),
                user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toUnmodifiableSet()),
                user.isActive(), user.getCreatedAt());
    }
}
