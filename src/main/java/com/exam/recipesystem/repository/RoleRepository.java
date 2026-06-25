package com.exam.recipesystem.repository;

import com.exam.recipesystem.entity.Role;
import com.exam.recipesystem.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
