package com.codetest.szsrestapi.global.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SzsScrapProperties {
    @Value("${config.scrap.url}")
    private String scrapUrl;
}
