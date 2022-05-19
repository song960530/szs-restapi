package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.entity.UserIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIpRepository extends JpaRepository<UserIp, Long> {
    Optional<UserIp> findByUser(User user);

    Optional<UserIp> findByUser_UserId(String userId);

}
