package com.codetest.szsrestapi.domain.user.repository;

import com.codetest.szsrestapi.domain.user.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
}
