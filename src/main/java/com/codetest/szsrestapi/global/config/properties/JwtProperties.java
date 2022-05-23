package com.codetest.szsrestapi.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
public class JwtProperties {
    @Value("${config.jwt.secretKey}")
    private final String secretKey;
    @Value("${config.jwt.validTime}")
    private final long tokenValidTime;
}
