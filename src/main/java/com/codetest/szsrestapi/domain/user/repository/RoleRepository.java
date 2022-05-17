package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoles(EnumRole roles);
}
