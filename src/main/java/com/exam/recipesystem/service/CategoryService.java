package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.CategoryRequest;
import com.exam.recipesystem.dto.CategoryResponse;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.exception.BadRequestException;
import com.exam.recipesystem.exception.DuplicateResourceException;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.CategoryRepository;
import com.exam.recipesystem.repository.RecipeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Category already exists");
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategory(id);
        categoryRepository.findByName(request.name())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new DuplicateResourceException("Category already exists");
                });
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getCategory(id);
        long activeRecipes = recipeRepository.countByCategoryAndStatus(category, RecipeStatus.ACTIVE);
        if (activeRecipes > 0) {
            throw new BadRequestException("Category cannot be deleted while it has active recipes");
        }
        category.setActive(false);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.isActive());
    }
}
