package com.exam.recipesystem.controller;

import com.exam.recipesystem.dto.CommentRequest;
import com.exam.recipesystem.dto.CommentResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/recipes/{recipeId}/comments")
    public List<CommentResponse> getByRecipe(@PathVariable Long recipeId) {
        return commentService.getByRecipe(recipeId);
    }

    @PostMapping("/recipes/{recipeId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@PathVariable Long recipeId,
                                  @Valid @RequestBody CommentRequest request,
                                  @AuthenticationPrincipal AppUser currentUser) {
        return commentService.create(recipeId, request, currentUser);
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse update(@PathVariable Long commentId,
                                  @Valid @RequestBody CommentRequest request,
                                  @AuthenticationPrincipal AppUser currentUser) {
        return commentService.update(commentId, request, currentUser);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId, @AuthenticationPrincipal AppUser currentUser) {
        commentService.delete(commentId, currentUser);
    }
}
