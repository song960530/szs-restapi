package com.codetest.szsrestapi.global.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request); // 헤더에서 JWT 받기

        // 토큰의 유효성 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);  // 토큰이 유효하면 유저 정보를 받아온다
            SecurityContextHolder.getContext().setAuthentication(authentication);   // SecurityContext에 Authentication 객체 저장
        }

        chain.doFilter(request, response);
    }
}
