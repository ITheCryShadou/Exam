package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RecipeStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findAllByUser(AppUser user);

    long countByCategoryAndStatus(Category category, RecipeStatus status);
}
