package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.entity.UserIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIpRepository extends JpaRepository<UserIp, Long> {
    Optional<UserIp> findByUser(User user);

    Optional<UserIp> findByUser_UserId(String userId);
}
