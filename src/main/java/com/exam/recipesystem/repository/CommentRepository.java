package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.Comment;
import com.exam.recipesystem.entity.Recipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByRecipeOrderByCreatedAtDesc(Recipe recipe);
}
