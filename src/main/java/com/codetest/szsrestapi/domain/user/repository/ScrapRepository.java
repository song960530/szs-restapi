package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.entity.Scrap;
import com.codetest.szsrestapi.domain.user.entity.ScrapHistory;
import com.codetest.szsrestapi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUserAndScrapHistory(User user, ScrapHistory scrapHistory);
}
