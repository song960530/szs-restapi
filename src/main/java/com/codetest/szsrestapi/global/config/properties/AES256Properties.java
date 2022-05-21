package com.codetest.szsrestapi.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
public class AES256Properties {
    @Value("${config.cipher.aesKey}")
    private final String key;
    @Value("${config.cipher.iv}")
    private final String iv;
}
