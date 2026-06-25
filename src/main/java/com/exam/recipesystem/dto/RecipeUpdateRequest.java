package com.exam.recipesystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecipeUpdateRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 3000) String description,
        @NotBlank @Size(max = 5000) String ingredients,
        @NotBlank @Size(max = 5000) String instructions,
        @NotNull @Min(1) @Max(1440) Integer cookingTimeMinutes,
        @NotNull Long categoryId) {
}
