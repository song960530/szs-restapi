package com.codetest.szsrestapi.global.config.jwt;

import com.codetest.szsrestapi.domain.member.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${config.jwt.secretKey}")
    private String secretKey;

    @Value("${config.jwt.validTime}")
    private long tokenValidTime;

    private String encSecretKey;


    @PostConstruct
    protected void init() {
        encSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes()); // 초기 secretKey Base64로 encrypt
    }

    // JWT 토큰 생성
    public String createToken(String userPk, List<Role> roles) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg", "HS256");

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보단위
        List<String> roleList = roles.stream().map(Role::getRoles).collect(Collectors.toList());
        claims.put("roles", roleList);

        return Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발생 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간
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
    // "X-AUTH-TOKEN" : "TOKEN값"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }
}
