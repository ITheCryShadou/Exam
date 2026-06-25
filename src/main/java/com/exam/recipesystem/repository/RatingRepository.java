package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Rating;
import com.exam.recipesystem.entity.Recipe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByUserAndRecipe(AppUser user, Recipe recipe);

    Optional<Rating> findByUserAndRecipe(AppUser user, Recipe recipe);

    @Query("select avg(r.value) from Rating r where r.recipe = :recipe")
    Double averageRatingByRecipe(@Param("recipe") Recipe recipe);

    @Query("select avg(r.value) from Rating r")
    Double averageRatingForAllRecipes();
}
