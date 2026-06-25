package com.exam.recipesystem.dto;

import com.exam.recipesystem.enums.RecipeStatus;
import java.time.LocalDateTime;

public record RecipeFilterRequest(
        Long categoryId,
        Long userId,
        RecipeStatus status,
        String keyword,
        Integer minRating,
        Integer maxCookingTimeMinutes,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore) {
}
