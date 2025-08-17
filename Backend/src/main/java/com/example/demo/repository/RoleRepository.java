// src/main/java/com/example/demo/repository/RoleRepository.java
package com.example.demo.repository;

import com.example.demo.model.roles.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRoleName(String roleName);

    // <<< YENİ METOT >>>
    Optional<Roles> findByRoleNameAndScope(String roleName, String scope);
}