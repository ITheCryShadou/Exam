package com.exam.recipesystem.config;

import com.exam.recipesystem.entity.AppUser;
import com.exam.recipesystem.entity.Category;
import com.exam.recipesystem.entity.Role;
import com.exam.recipesystem.enums.RoleName;
import com.exam.recipesystem.repository.CategoryRepository;
import com.exam.recipesystem.repository.RoleRepository;
import com.exam.recipesystem.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.USER)));
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ADMIN)));

        createDefaultCategories();
        createAdminIfConfigured(userRole, adminRole);
    }

    private void createDefaultCategories() {
        List.of(
                        new CategorySeed("Breakfast", "Morning recipes and simple meals"),
                        new CategorySeed("Main dishes", "Lunch, dinner and everyday cooking"),
                        new CategorySeed("Desserts", "Sweet recipes and baking"),
                        new CategorySeed("Healthy", "Light and balanced recipes"))
                .forEach(seed -> {
                    if (!categoryRepository.existsByNameIgnoreCase(seed.name())) {
                        categoryRepository.save(Category.builder()
                                .name(seed.name())
                                .description(seed.description())
                                .active(true)
                                .build());
                    }
                });
    }

    private void createAdminIfConfigured(Role userRole, Role adminRole) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(AppUser.builder()
                    .username(adminUsername.isBlank() ? "admin" : adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .blocked(false)
                    .roles(new HashSet<>(List.of(userRole, adminRole)))
                    .build());
        }
    }

    private record CategorySeed(String name, String description) {
    }
}
