package com.codetest.szsrestapi.global.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final String API_NAME = "SZS-RESTAPI";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "삼쩜삼(자비스앤빌런즈)의 백엔드 엔지니어 포지션 과제_송문준";
    private static final String API_BASEPACKAGE = "com.codetest.szsrestapi";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false) // 기본 응답 상태코드 삭제
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey())) // header를 전역적으로 사용할수있도록 설정
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(API_BASEPACKAGE))  // Swagger를 적용할 클래스의 package명
                .paths(PathSelectors.any())  // 해당 package 하위에 있는 모든 url에 적용
                .build();
    }

    public SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    public List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }


    public ApiInfo apiInfo() {  // API의 이름, 현재 버전, API에 대한 정보
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .build();
    }

    public ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}
