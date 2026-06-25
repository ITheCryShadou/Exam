package com.exam.recipesystem.dto;

import com.exam.recipesystem.enums.RecipeStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull RecipeStatus status) {
}
