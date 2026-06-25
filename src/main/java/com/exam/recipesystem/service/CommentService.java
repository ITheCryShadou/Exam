package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.CommentRequest;
import com.exam.recipesystem.dto.CommentResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Comment;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RoleName;
import com.exam.recipesystem.exception.BlockedUserException;
import com.exam.recipesystem.exception.ForbiddenException;
import com.exam.recipesystem.exception.ResourceNotFoundException;
import com.exam.recipesystem.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeService recipeService;

    @Transactional
    public CommentResponse create(Long recipeId, CommentRequest request, AppUser currentUser) {
        ensureNotBlocked(currentUser);
        Recipe recipe = recipeService.getRecipe(recipeId);
        Comment comment = Comment.builder()
                .content(request.content())
                .recipe(recipe)
                .user(currentUser)
                .build();
        return toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getByRecipe(Long recipeId) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        return commentRepository.findAllByRecipeOrderByCreatedAtDesc(recipe).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CommentResponse update(Long commentId, CommentRequest request, AppUser currentUser) {
        Comment comment = getComment(commentId);
        ensureOwnerOrAdmin(comment, currentUser);
        comment.setContent(request.content());
        return toResponse(comment);
    }

    @Transactional
    public void delete(Long commentId, AppUser currentUser) {
        Comment comment = getComment(commentId);
        ensureOwnerOrAdmin(comment, currentUser);
        commentRepository.delete(comment);
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getRecipe().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    private void ensureOwnerOrAdmin(Comment comment, AppUser user) {
        if (!comment.getUser().getId().equals(user.getId()) && !isAdmin(user)) {
            throw new ForbiddenException("You can manage only your own comments");
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
