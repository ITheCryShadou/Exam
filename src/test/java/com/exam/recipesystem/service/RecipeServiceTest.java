package com.exam.recipesystem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.exam.recipesystem.dto.RecipeUpdateRequest;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.exception.ForbiddenException;
import com.exam.recipesystem.repository.RatingRepository;
import com.exam.recipesystem.repository.RecipeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void userCannotUpdateAnotherUsersRecipe() {
        AppUser owner = AppUser.builder().id(1L).email("owner@test.com").blocked(false).build();
        AppUser anotherUser = AppUser.builder().id(2L).email("other@test.com").blocked(false).build();
        Recipe recipe = Recipe.builder()
                .id(10L)
                .title("Original")
                .description("Original description")
                .ingredients("Original ingredients")
                .instructions("Original instructions")
                .cookingTimeMinutes(25)
                .status(RecipeStatus.ACTIVE)
                .user(owner)
                .category(Category.builder().id(3L).name("Main dishes").active(true).build())
                .build();

        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        assertThrows(ForbiddenException.class,
                () -> recipeService.update(10L,
                        new RecipeUpdateRequest(
                                "New",
                                "New description",
                                "New ingredients",
                                "New instructions",
                                30,
                                3L),
                        anotherUser));

        verify(recipeRepository).findById(10L);
    }
}
