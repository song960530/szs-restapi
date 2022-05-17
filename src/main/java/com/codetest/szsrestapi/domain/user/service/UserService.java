package com.codetest.szsrestapi.domain.user.service;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.LoginReqDto;
import com.codetest.szsrestapi.domain.user.dto.response.LoginResDto;
import com.codetest.szsrestapi.domain.user.dto.response.UserInfoDto;
import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.repository.RoleRepository;
import com.codetest.szsrestapi.domain.user.repository.UserRepository;
import com.codetest.szsrestapi.global.config.jwt.JwtTokenProvider;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AES256Util aes256Util;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResultMessage signup(JoinReqDto requestDto) {
        requestDto.setRole(roleRepository.findByRoles(EnumRole.ROLE_USER).orElseThrow(
                () -> new IllegalArgumentException("권한 정보를 찾을 수 없습니다")
        ));
        requestDto.setEncPassword(passwordEncoder.encode(requestDto.getPassword()));
        requestDto.setEncRegNo(aes256Util.encrypt(requestDto.getRegNo()));

        userRepository.save(requestDto.toEntity());

        return ResultMessage.of("회원가입", HttpStatus.OK);
    }

    public ResultMessage login(LoginReqDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("패스워드가 맞지 않습니다");

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRoles());

        return ResultMessage.of(new LoginResDto(token, "BEARER"), "로그인", HttpStatus.OK);
    }

    public ResultMessage whoAmI() {
        User user = findUserIdFromAuth();
        UserInfoDto userInfoDto = UserInfoDto.creatDto(user.getUserNo(), user.getUserId(), user.getName(), aes256Util.decrypt(user.getRegNo()));
        return ResultMessage.of(userInfoDto, "내정보", HttpStatus.OK);
    }

    public User findUserIdFromAuth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다"));
    }
}
