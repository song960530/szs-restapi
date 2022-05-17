package com.codetest.szsrestapi.global.config;

import com.codetest.szsrestapi.global.config.response.CustomObjectMapper;
import com.codetest.szsrestapi.global.config.restapi.HeaderRequestInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(customObjectMapper()));
    }

    @Bean
    public ObjectMapper customObjectMapper() {
        return new CustomObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // interceptor를 사용하여 Header에 공통적으로 들어가는 부분들 처리
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
        interceptors.add(new HeaderRequestInterceptor("Content-type", MediaType.APPLICATION_JSON_VALUE));

        // Object 변환을 위한 MessageConverter 설정 추가
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        return builder.additionalInterceptors(interceptors)
                .messageConverters(messageConverters)
                .build();
    }
}

