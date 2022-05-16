package com.codetest.szsrestapi.domain.member.service;

import com.codetest.szsrestapi.domain.member.dto.request.JoinRequestDto;
import com.codetest.szsrestapi.domain.member.entity.Role;
import com.codetest.szsrestapi.domain.member.repository.MemberRepository;
import com.codetest.szsrestapi.domain.member.repository.RoleRepository;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public ResultMessage signup(JoinRequestDto requestDto) {
        roleRepository.save(new Role(1L, "ROLE_USER"));

        requestDto.setRole(new Role(1L, "ROLE_USER"));
        requestDto.setEncPassword("test1234");
        memberRepository.save(requestDto.toEntity());
        return ResultMessage.of("회원가입", HttpStatus.OK);
    }
}
