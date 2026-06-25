package com.exam.recipesystem.controller;

import com.exam.recipesystem.dto.RatingRequest;
import com.exam.recipesystem.dto.RatingResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse create(@PathVariable Long recipeId,
                                 @Valid @RequestBody RatingRequest request,
                                 @AuthenticationPrincipal AppUser currentUser) {
        return ratingService.create(recipeId, request, currentUser);
    }

    @PutMapping
    public RatingResponse update(@PathVariable Long recipeId,
                                 @Valid @RequestBody RatingRequest request,
                                 @AuthenticationPrincipal AppUser currentUser) {
        return ratingService.update(recipeId, request, currentUser);
    }

    @GetMapping("/average")
    public Double average(@PathVariable Long recipeId) {
        return ratingService.getAverage(recipeId);
    }
}
