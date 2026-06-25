package com.exam.recipesystem.service;

import com.exam.recipesystem.dto.AuthResponse;
import com.exam.recipesystem.dto.LoginRequest;
import com.exam.recipesystem.dto.RegisterRequest;
import com.exam.recipesystem.dto.UserResponse;
import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Role;
import com.exam.recipesystem.enums.RoleName;
import com.exam.recipesystem.exception.DuplicateResourceException;
import com.exam.recipesystem.repository.RoleRepository;
import com.exam.recipesystem.repository.UserRepository;
import com.exam.recipesystem.security.JwtService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already used");
        }
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.USER)));

        AppUser user = AppUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .blocked(false)
                .roles(Set.of(userRole))
                .build();

        AppUser saved = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(saved), toUserResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AppUser user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(user), toUserResponse(user));
    }

    public UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isBlocked(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
    }
}
