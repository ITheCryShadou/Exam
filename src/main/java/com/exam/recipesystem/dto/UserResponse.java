package com.exam.recipesystem.dto;

import java.util.Set;

public record UserResponse(Long id, String username, String email, boolean blocked, Set<String> roles) {
}
