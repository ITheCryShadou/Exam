package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Favorite;
import com.exam.recipesystem.entity.Recipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndRecipe(AppUser user, Recipe recipe);

    Optional<Favorite> findByUserAndRecipe(AppUser user, Recipe recipe);

    List<Favorite> findAllByUserOrderByCreatedAtDesc(AppUser user);
}
