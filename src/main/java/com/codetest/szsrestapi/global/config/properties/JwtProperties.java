package com.codetest.szsrestapi.global.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtProperties {
    @Value("${config.jwt.secretKey}")
    private String secretKey;
    @Value("${config.jwt.apiKey}")
    private String apiKey;
    @Value("${config.jwt.validTime}")
    private long tokenValidTime;
}
