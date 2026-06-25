package com.exam.recipesystem.dto;

public record StatisticsResponse(
        long totalUsers,
        long totalRecipes,
        long totalComments,
        long totalFavorites,
        Double averageRating) {
}
