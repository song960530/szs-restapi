package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapHistoryRepository extends JpaRepository<ScrapHistory, Long> {
    Optional<ScrapHistory> findTopByUser(User user);
}
