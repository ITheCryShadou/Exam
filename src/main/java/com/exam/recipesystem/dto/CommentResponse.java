package com.exam.recipesystem.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long recipeId,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
