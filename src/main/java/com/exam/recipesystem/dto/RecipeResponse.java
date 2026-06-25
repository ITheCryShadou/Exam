package com.exam.recipesystem.dto;

import com.exam.recipesystem.enums.RecipeStatus;
import java.time.LocalDateTime;

public record RecipeResponse(
        Long id,
        String title,
        String description,
        String ingredients,
        String instructions,
        Integer cookingTimeMinutes,
        RecipeStatus status,
        Long categoryId,
        String categoryName,
        Long userId,
        String username,
        Double averageRating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
