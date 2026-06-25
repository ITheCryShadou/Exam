package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.AdminUserResponse;
import com.exam.recipesystem.dto.RecipeResponse;
import com.exam.recipesystem.dto.StatisticsResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.CommentRepository;
import com.exam.recipesystem.repository.FavoriteRepository;
import com.exam.recipesystem.repository.RatingRepository;
import com.exam.recipesystem.repository.RecipeRepository;
import com.exam.recipesystem.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final RecipeService recipeService;

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toAdminUserResponse)
                .toList();
    }

    @Transactional
    public AdminUserResponse blockUser(Long userId) {
        AppUser user = getUser(userId);
        user.setBlocked(true);
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse activateUser(Long userId) {
        AppUser user = getUser(userId);
        user.setBlocked(false);
        return toAdminUserResponse(user);
    }

    @Transactional
    public void deleteAnyRecipe(Long id) {
        recipeRepository.delete(recipeService.getRecipe(id));
    }

    @Transactional
    public RecipeResponse changeRecipeStatus(Long id, RecipeStatus status) {
        return recipeService.changeStatus(id, status);
    }

    public StatisticsResponse getStatistics() {
        Double averageRating = ratingRepository.averageRatingForAllRecipes();
        return new StatisticsResponse(
                userRepository.count(),
                recipeRepository.count(),
                commentRepository.count(),
                favoriteRepository.count(),
                averageRating == null ? 0.0 : averageRating);
    }

    private AppUser getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AdminUserResponse toAdminUserResponse(AppUser user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isBlocked(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
