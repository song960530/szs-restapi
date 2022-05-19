package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.api.enums.EnumRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoles(EnumRole roles);
}
