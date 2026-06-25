package com.exam.recipesystem.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        boolean blocked,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
