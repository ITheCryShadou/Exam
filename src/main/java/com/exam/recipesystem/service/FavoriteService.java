package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.FavoriteResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Favorite;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.exception.BlockedUserException;
import com.exam.recipesystem.exception.DuplicateResourceException;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.FavoriteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RecipeService recipeService;

    @Transactional
    public FavoriteResponse add(Long recipeId, AppUser currentUser) {
        ensureNotBlocked(currentUser);
        Recipe recipe = recipeService.getRecipe(recipeId);
        if (favoriteRepository.existsByUserAndRecipe(currentUser, recipe)) {
            throw new DuplicateResourceException("Recipe is already in favorites");
        }
        Favorite favorite = Favorite.builder()
                .recipe(recipe)
                .user(currentUser)
                .build();
        return toResponse(favoriteRepository.save(favorite));
    }

    @Transactional
    public void remove(Long recipeId, AppUser currentUser) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        Favorite favorite = favoriteRepository.findByUserAndRecipe(currentUser, recipe)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }

    public List<FavoriteResponse> getMyFavorites(AppUser currentUser) {
        return favoriteRepository.findAllByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(this::toResponse)
                .toList();
    }

    private FavoriteResponse toResponse(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getRecipe().getId(),
                favorite.getRecipe().getTitle(),
                favorite.getUser().getId(),
                favorite.getCreatedAt());
    }

    private void ensureNotBlocked(AppUser user) {
        if (user.isBlocked()) {
            throw new BlockedUserException("Blocked user cannot perform this action");
        }
    }
}
