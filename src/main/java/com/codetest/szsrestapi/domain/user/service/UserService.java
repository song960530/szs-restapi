package com.codetest.szsrestapi.domain.user.service;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.LoginReqDto;
import com.codetest.szsrestapi.domain.user.dto.response.LoginResDto;
import com.codetest.szsrestapi.domain.user.dto.response.UserInfoDto;
import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.exception.UserException;
import com.codetest.szsrestapi.domain.user.repository.RoleRepository;
import com.codetest.szsrestapi.domain.user.repository.UserRepository;
import com.codetest.szsrestapi.global.annotation.LoginCheck;
import com.codetest.szsrestapi.global.config.jwt.JwtTokenProvider;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AES256Util aes256Util;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Transactional
    public void signup(JoinReqDto requestDto) {
        requestDto.setRole(roleRepository.findByRoles(EnumRole.ROLE_USER).orElseThrow(
                () -> new UserException("권한 정보를 찾을 수 없습니다")
        ));
        requestDto.setEncPassword(passwordEncoder.encode(requestDto.getPassword()));
        requestDto.setEncRegNo(aes256Util.encrypt(requestDto.getRegNo()));

        userRepository.save(requestDto.toEntity());
    }

    public LoginResDto login(LoginReqDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("패스워드가 맞지 않습니다");

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRoles());

        return new LoginResDto(token, "BEARER");
    }

    @LoginCheck
    public UserInfoDto whoAmI() {
        User user = findUserIdFromAuth();

        return UserInfoDto.creatDto(user.getUserNo(), user.getUserId(), user.getName(), aes256Util.decrypt(user.getRegNo()));
    }

    public User findUserIdFromAuth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다"));
    }

    public void scrap() {
//        restTemplate.postForEntity()
    }

    public URI convertRequestURI(Object request) {
        UriComponentsBuilder builder = null;
        MultiValueMap params = new LinkedMultiValueMap<>();

//        if ( request instanceof LmsContent )
//        {
//            builder = UriComponentsBuilder.fromHttpUrl(lmsConfig.getContentsUrl());
//        }
//        else if ( request instanceof LmsAttendance )
//        {
//            builder = UriComponentsBuilder.fromHttpUrl(lmsConfig.getCompletionUrl());
//        }

        params.setAll(new ObjectMapper().convertValue(request, Map.class));
        builder.queryParams(params);

        return builder.build()
                .toUri();
    }
}
