package com.codetest.szsrestapi.global.config;

import com.codetest.szsrestapi.global.config.restapi.HeaderRequestInterceptor;
import com.codetest.szsrestapi.global.resolver.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // interceptor를 사용하여 Header에 공통적으로 들어가는 부분들 처리
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
        interceptors.add(new HeaderRequestInterceptor("ContentType", MediaType.APPLICATION_JSON_VALUE));

        return builder.additionalInterceptors(interceptors)
                .build();
    }
}

