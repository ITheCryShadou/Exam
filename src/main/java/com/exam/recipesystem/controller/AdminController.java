package com.exam.recipesystem.controller;

import com.exam.recipesystem.dto.AdminUserResponse;
import com.exam.recipesystem.dto.ChangeStatusRequest;
import com.exam.recipesystem.dto.RecipeResponse;
import com.exam.recipesystem.dto.StatisticsResponse;
import com.exam.recipesystem.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<AdminUserResponse> users() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{id}/block")
    public AdminUserResponse blockUser(@PathVariable Long id) {
        return adminService.blockUser(id);
    }

    @PutMapping("/users/{id}/activate")
    public AdminUserResponse activateUser(@PathVariable Long id) {
        return adminService.activateUser(id);
    }

    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) {
        adminService.deleteAnyRecipe(id);
    }

    @PutMapping("/recipes/{id}/status")
    public RecipeResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest request) {
        return adminService.changeRecipeStatus(id, request.status());
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return adminService.getStatistics();
    }
}
