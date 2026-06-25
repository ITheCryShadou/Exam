package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
