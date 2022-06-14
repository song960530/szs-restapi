package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT U FROM User U JOIN FETCH U.roles JOIN FETCH U.userIp")
    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByRegNo(String regNo);
}
