package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.RecipeCreateRequest;
import com.exam.recipesystem.dto.RecipeResponse;
import com.exam.recipesystem.dto.RecipeUpdateRequest;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.enums.RoleName;
import com.exam.recipesystem.exception.BlockedUserException;
import com.exam.recipesystem.exception.ForbiddenException;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.RatingRepository;
import com.exam.recipesystem.repository.RecipeRepository;
import com.exam.recipesystem.specification.RecipeSpecification;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;
    private final CategoryService categoryService;

    @Transactional
    public RecipeResponse create(RecipeCreateRequest request, AppUser currentUser) {
        ensureNotBlocked(currentUser);
        Category category = getActiveCategory(request.categoryId());
        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .ingredients(request.ingredients())
                .instructions(request.instructions())
                .cookingTimeMinutes(request.cookingTimeMinutes())
                .category(category)
                .user(currentUser)
                .status(RecipeStatus.ACTIVE)
                .build();
        return toResponse(recipeRepository.save(recipe));
    }

    @Transactional
    public RecipeResponse update(Long id, RecipeUpdateRequest request, AppUser currentUser) {
        Recipe recipe = getRecipe(id);
        ensureOwnerOrAdmin(recipe, currentUser);
        Category category = getActiveCategory(request.categoryId());
        recipe.setTitle(request.title());
        recipe.setDescription(request.description());
        recipe.setIngredients(request.ingredients());
        recipe.setInstructions(request.instructions());
        recipe.setCookingTimeMinutes(request.cookingTimeMinutes());
        recipe.setCategory(category);
        return toResponse(recipe);
    }

    @Transactional
    public void archive(Long id, AppUser currentUser) {
        Recipe recipe = getRecipe(id);
        ensureOwnerOrAdmin(recipe, currentUser);
        recipe.setStatus(RecipeStatus.ARCHIVED);
    }

    @Transactional
    public RecipeResponse changeStatus(Long id, RecipeStatus status) {
        Recipe recipe = getRecipe(id);
        recipe.setStatus(status);
        return toResponse(recipe);
    }

    public RecipeResponse getById(Long id) {
        return toResponse(getRecipe(id));
    }

    public Page<RecipeResponse> getAll(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            Long categoryId,
            Long userId,
            RecipeStatus status,
            String keyword,
            Integer minRating,
            Integer maxCookingTimeMinutes,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore) {
        Sort sort = Sort.by(resolveSortField(sortBy));
        sort = "asc".equalsIgnoreCase(sortDirection) ? sort.ascending() : sort.descending();
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);
        return recipeRepository.findAll(
                        RecipeSpecification.withFilters(
                                categoryId,
                                userId,
                                status,
                                keyword,
                                minRating,
                                maxCookingTimeMinutes,
                                createdAfter,
                                createdBefore),
                        pageRequest)
                .map(this::toResponse);
    }

    public List<RecipeResponse> getMyRecipes(AppUser currentUser) {
        return recipeRepository.findAllByUser(currentUser).stream()
                .map(this::toResponse)
                .toList();
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
    }

    public RecipeResponse toResponse(Recipe recipe) {
        Double averageRating = ratingRepository.averageRatingByRecipe(recipe);
        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                recipe.getCookingTimeMinutes(),
                recipe.getStatus(),
                recipe.getCategory().getId(),
                recipe.getCategory().getName(),
                recipe.getUser().getId(),
                recipe.getUser().getUsername(),
                averageRating == null ? 0.0 : averageRating,
                recipe.getCreatedAt(),
                recipe.getUpdatedAt());
    }

    private Category getActiveCategory(Long categoryId) {
        Category category = categoryService.getCategory(categoryId);
        if (!category.isActive()) {
            throw new ResourceNotFoundException("Category is not active");
        }
        return category;
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        return switch (sortBy) {
            case "title", "createdAt", "updatedAt", "status", "cookingTimeMinutes" -> sortBy;
            default -> "createdAt";
        };
    }

    private void ensureOwnerOrAdmin(Recipe recipe, AppUser user) {
        if (!recipe.getUser().getId().equals(user.getId()) && !isAdmin(user)) {
            throw new ForbiddenException("You can manage only your own recipes");
        }
    }

    private boolean isAdmin(AppUser user) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.ADMIN);
    }

    private void ensureNotBlocked(AppUser user) {
        if (user.isBlocked()) {
            throw new BlockedUserException("Blocked user cannot perform this action");
        }
    }
}
