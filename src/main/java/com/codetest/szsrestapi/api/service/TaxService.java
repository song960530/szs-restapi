package com.codetest.szsrestapi.api.service;

import com.codetest.szsrestapi.api.dto.response.RefoundResDto;
import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface TaxService {
    public Map<String, Object> scrap(User user, ResponseEntity<Object> apiResponse, ScrapHistory scrapHistory);

    ResponseEntity<Object> callScrapApi(User user);

    @Transactional
    ScrapHistory recordScrapHistory(User user, ResponseEntity<Object> apiResponse);

    public RefoundResDto refund(User user);
}
