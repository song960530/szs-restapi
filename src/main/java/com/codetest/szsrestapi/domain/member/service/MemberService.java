package com.codetest.szsrestapi.domain.member.service;

import com.codetest.szsrestapi.domain.member.EnumRole;
import com.codetest.szsrestapi.domain.member.dto.request.JoinRequestDto;
import com.codetest.szsrestapi.domain.member.repository.MemberRepository;
import com.codetest.szsrestapi.domain.member.repository.RoleRepository;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AES256Util aes256Util;

    @Transactional
    public ResultMessage signup(JoinRequestDto requestDto) {
        requestDto.setRole(roleRepository.findByRoles(EnumRole.ROLE_USER).orElseThrow(
                () -> new IllegalArgumentException("권한 정보를 찾을 수 없습니다")
        ));
        requestDto.setEncPassword(passwordEncoder.encode(requestDto.getPassword()));
        requestDto.setEncRegNo(aes256Util.encrypt(requestDto.getRegNo()));

        memberRepository.save(requestDto.toEntity());

        return ResultMessage.of("회원가입", HttpStatus.OK);
    }
}
