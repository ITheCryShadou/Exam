package com.exam.recipesystem.controller;

import com.exam.recipesystem.dto.FavoriteResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/recipes/{recipeId}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@PathVariable Long recipeId, @AuthenticationPrincipal AppUser currentUser) {
        return favoriteService.add(recipeId, currentUser);
    }

    @DeleteMapping("/recipes/{recipeId}/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long recipeId, @AuthenticationPrincipal AppUser currentUser) {
        favoriteService.remove(recipeId, currentUser);
    }

    @GetMapping("/favorites/my")
    public List<FavoriteResponse> myFavorites(@AuthenticationPrincipal AppUser currentUser) {
        return favoriteService.getMyFavorites(currentUser);
    }
}
