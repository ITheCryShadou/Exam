package com.exam.recipesystem.dto;

import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id,
        Long recipeId,
        String recipeTitle,
        Long userId,
        LocalDateTime createdAt) {
}
