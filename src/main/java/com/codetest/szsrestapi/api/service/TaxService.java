package com.codetest.szsrestapi.api.service;

import com.codetest.szsrestapi.api.dto.response.RefoundResDto;
import com.codetest.szsrestapi.api.exception.ScrapApiException;

import java.util.Map;

public interface TaxService {
    public Map<String, Object> scrap() throws ScrapApiException;

    public RefoundResDto refund();
}
