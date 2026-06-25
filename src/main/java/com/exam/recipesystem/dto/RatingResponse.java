package com.exam.recipesystem.dto;

public record RatingResponse(Long id, Long recipeId, Long userId, int value) {
}
