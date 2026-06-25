package com.exam.recipesystem.controller;

import com.exam.recipesystem.dto.RecipeCreateRequest;
import com.exam.recipesystem.dto.RecipeResponse;
import com.exam.recipesystem.dto.RecipeUpdateRequest;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.service.RecipeService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public Page<RecipeResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) RecipeStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxCookingTimeMinutes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore) {
        return recipeService.getAll(
                page,
                size,
                sortBy,
                sortDirection,
                categoryId,
                userId,
                status,
                keyword,
                minRating,
                maxCookingTimeMinutes,
                createdAfter,
                createdBefore);
    }

    @GetMapping("/{id}")
    public RecipeResponse getById(@PathVariable Long id) {
        return recipeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse create(@Valid @RequestBody RecipeCreateRequest request,
                                 @AuthenticationPrincipal AppUser currentUser) {
        return recipeService.create(request, currentUser);
    }

    @PutMapping("/{id}")
    public RecipeResponse update(@PathVariable Long id,
                                 @Valid @RequestBody RecipeUpdateRequest request,
                                 @AuthenticationPrincipal AppUser currentUser) {
        return recipeService.update(id, request, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable Long id, @AuthenticationPrincipal AppUser currentUser) {
        recipeService.archive(id, currentUser);
    }

    @GetMapping("/my")
    public List<RecipeResponse> myRecipes(@AuthenticationPrincipal AppUser currentUser) {
        return recipeService.getMyRecipes(currentUser);
    }
}
