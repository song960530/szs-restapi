package com.codetest.szsrestapi.global.config.jwt;

import com.codetest.szsrestapi.api.enums.EnumRole;
import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.global.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;
    private String encSecretKey;
    private String headerName;


    @PostConstruct
    public void init() {
        encSecretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes()); // 초기 secretKey Base64로 encrypt
        headerName = "Authorization";
    }

    // JWT 토큰 생성
    public String createToken(String userPk, List<Role> roles) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보단위
        List<EnumRole> roleList = roles.stream().map(Role::getRoles).collect(Collectors.toList());
        claims.put("roles", roleList);
        claims.put("apiKey", jwtProperties.getApiKey());

        return Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발생 시간 정보
                .setExpiration(new Date(now.getTime() + jwtProperties.getTokenValidTime())) // 만료시간
                .signWith(SignatureAlgorithm.HS256, encSecretKey) // 암호화 및 encSecretKey 세팅
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser()
                .setSigningKey(encSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // request header에서 token 값을 가져온다
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(headerName);

        if (!StringUtils.hasText(token))
            // throw new IllegalArgumentException("조회된 토큰값이 없습니다");
            return "";

        if (!Pattern.matches("^Bearer .*", token))
            throw new IllegalArgumentException("토큰값이 잘못되었습니다");

        return token.replaceAll("^Bearer( )*", "");
    }

    // 토큰의 유효성과 만료일자 확인
    public boolean validToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(encSecretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();

            String receiveKey = String.valueOf(claims.get("apiKey"));

            if (!StringUtils.hasText(receiveKey) || !receiveKey.equals(jwtProperties.getApiKey()))
                throw new IllegalArgumentException("정상적인 API 키가 아닙니다");

            // 만료됐으면 true, 아니면 false
            if (claims.getExpiration().before(new Date()))
                throw new IllegalStateException("만료된 토큰입니다");

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
