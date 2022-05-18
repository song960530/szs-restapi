package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.entity.ScrapHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapHistoryRepository extends JpaRepository<ScrapHistory,Long> {
}
