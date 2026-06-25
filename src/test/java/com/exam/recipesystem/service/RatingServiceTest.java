package com.exam.recipesystem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.exam.recipesystem.dto.RatingRequest;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RecipeStatus;
import com.exam.recipesystem.exception.DuplicateResourceException;
import com.exam.recipesystem.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void userCannotRateSameRecipeTwice() {
        AppUser user = AppUser.builder().id(1L).email("user@test.com").blocked(false).build();
        Recipe recipe = Recipe.builder()
                .id(10L)
                .title("Pasta")
                .description("Description")
                .ingredients("Pasta")
                .instructions("Boil")
                .cookingTimeMinutes(15)
                .status(RecipeStatus.ACTIVE)
                .user(user)
                .category(Category.builder().id(2L).name("Main dishes").active(true).build())
                .build();

        when(recipeService.getRecipe(10L)).thenReturn(recipe);
        when(ratingRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> ratingService.create(10L, new RatingRequest(5), user));

        verify(recipeService).getRecipe(10L);
        verify(ratingRepository).existsByUserAndRecipe(user, recipe);
        verifyNoMoreInteractions(ratingRepository);
    }
}
