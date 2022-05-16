package com.codetest.szsrestapi.domain.member.repository;

import com.codetest.szsrestapi.domain.member.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
