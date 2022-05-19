package com.codetest.szsrestapi.api.repository;

import com.codetest.szsrestapi.api.entity.Scrap;
import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUserAndScrapHistory(User user, ScrapHistory scrapHistory);
}
