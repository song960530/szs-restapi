package com.codetest.szsrestapi.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
public class SzsScrapProperties {
    @Value("${config.scrap.url}")
    private final String scrapUrl;
}
