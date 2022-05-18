package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.entity.ScrapHistory;
import com.codetest.szsrestapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapHistoryRepository extends JpaRepository<ScrapHistory, Long> {
    Optional<ScrapHistory> findTopByUser(User user);
}
