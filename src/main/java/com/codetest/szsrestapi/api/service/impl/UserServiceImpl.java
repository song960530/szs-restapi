package com.codetest.szsrestapi.api.service.impl;

import com.codetest.szsrestapi.api.dto.request.JoinReqDto;
import com.codetest.szsrestapi.api.dto.request.LoginReqDto;
import com.codetest.szsrestapi.api.dto.response.LoginResDto;
import com.codetest.szsrestapi.api.dto.response.UserInfoDto;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.entity.UserIp;
import com.codetest.szsrestapi.api.enums.EnumRole;
import com.codetest.szsrestapi.api.exception.UserException;
import com.codetest.szsrestapi.api.repository.RoleRepository;
import com.codetest.szsrestapi.api.repository.UserIpRepository;
import com.codetest.szsrestapi.api.repository.UserRepository;
import com.codetest.szsrestapi.api.service.UserService;
import com.codetest.szsrestapi.global.annotation.LoginCheck;
import com.codetest.szsrestapi.global.config.jwt.JwtTokenProvider;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AES256Util aes256Util;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserIpRepository userIpRepository;

    @Override
    @Transactional
    public User signup(JoinReqDto requestDto) {
        limitSingup(requestDto); // 임시 회원가입 제한

        requestDto.setRole(roleRepository.findByRoles(EnumRole.ROLE_USER).orElseThrow(
                () -> new UserException("권한 정보를 찾을 수 없습니다")
        ));
        requestDto.setEncPassword(passwordEncoder.encode(requestDto.getPassword()));
        requestDto.setEncRegNo(aes256Util.encrypt(requestDto.getRegNo()));

        if (userRepository.existsByUserId(requestDto.getUserId()))
            throw new IllegalStateException("중복된 ID 입니다");

        if (userRepository.existsByRegNo(requestDto.getEncRegNo()))
            throw new IllegalStateException("이미 가입된 회원입니다");

        return userRepository.save(requestDto.toEntity());
    }

    public void limitSingup(JoinReqDto requestDto) {
        Map<String, String> group = new HashMap<>();
        group.put("홍길동", "860824-1655068");
        group.put("김둘리", "921108-1582816");
        group.put("마징가", "880601-2455116");
        group.put("베지터", "910411-1656116");
        group.put("손오공", "820326-2715702");

        String regNo = group.get(requestDto.getName());

        if (!StringUtils.hasText(regNo) || !regNo.equals(requestDto.getRegNo()))
            throw new IllegalArgumentException("지정회원 외엔 가입이 불가능합니다");
    }

    @Override
    @Transactional
    public LoginResDto login(LoginReqDto requestDto, HttpServletRequest request) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("패스워드가 맞지 않습니다");

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRoles());

        recordUserIp(request, user);

        return new LoginResDto(token, "BEARER");
    }

    @Transactional
    public void recordUserIp(HttpServletRequest request, User user) {
        String ip = findClientIp(request);

        UserIp userIp = findUserLoginIp(user.getUserId());

        if (userIp == null) {
            userIpRepository.save(new UserIp(user, ip));
        } else {
            userIp.changeLoginIp(ip);
        }
    }

    @Override
    @LoginCheck
    public UserInfoDto whoAmI() {
        User user = findUserIdFromAuth();

        return UserInfoDto.creatDto(user.getUserNo(), user.getUserId(), user.getName(), aes256Util.decrypt(user.getRegNo()));
    }

    @Override
    public UserIp findUserLoginIp(String userId) {
        return userIpRepository.findByUser_UserId(userId).orElse(null);
    }

    @Override
    public String findClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR"); // 프록시나 로드 밸런서를 통해 들어왔을 경우 원주소 뽑아내기

        if (!StringUtils.hasText(ip))
            ip = request.getRemoteAddr();
        return ip;
    }

    public User findUserIdFromAuth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );
    }
}
