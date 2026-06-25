package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.RatingRequest;
import com.exam.recipesystem.dto.RatingResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Rating;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.exception.BlockedUserException;
import com.exam.recipesystem.exception.DuplicateResourceException;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeService recipeService;

    @Transactional
    public RatingResponse create(Long recipeId, RatingRequest request, AppUser currentUser) {
        ensureNotBlocked(currentUser);
        Recipe recipe = recipeService.getRecipe(recipeId);
        if (ratingRepository.existsByUserAndRecipe(currentUser, recipe)) {
            throw new DuplicateResourceException("User already rated this recipe");
        }
        Rating rating = Rating.builder()
                .recipe(recipe)
                .user(currentUser)
                .value(request.value())
                .build();
        return toResponse(ratingRepository.save(rating));
    }

    @Transactional
    public RatingResponse update(Long recipeId, RatingRequest request, AppUser currentUser) {
        ensureNotBlocked(currentUser);
        Recipe recipe = recipeService.getRecipe(recipeId);
        Rating rating = ratingRepository.findByUserAndRecipe(currentUser, recipe)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
        rating.setValue(request.value());
        return toResponse(rating);
    }

    public Double getAverage(Long recipeId) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        Double average = ratingRepository.averageRatingByRecipe(recipe);
        return average == null ? 0.0 : average;
    }

    private RatingResponse toResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getRecipe().getId(),
                rating.getUser().getId(),
                rating.getValue());
    }

    private void ensureNotBlocked(AppUser user) {
        if (user.isBlocked()) {
            throw new BlockedUserException("Blocked user cannot perform this action");
        }
    }
}
