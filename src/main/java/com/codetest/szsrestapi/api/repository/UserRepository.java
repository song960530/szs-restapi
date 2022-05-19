package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByRegNo(String regNo);
}
