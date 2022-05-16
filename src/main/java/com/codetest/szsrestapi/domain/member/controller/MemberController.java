package com.codetest.szsrestapi.domain.member.controller;

import com.codetest.szsrestapi.domain.member.dto.request.JoinRequestDto;
import com.codetest.szsrestapi.domain.member.service.MemberService;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/szs/signup")
    public ResponseEntity<ResultMessage> signup(@Valid @RequestBody JoinRequestDto requestDto) {
        return new ResponseEntity<>(memberService.signup(requestDto), HttpStatus.OK);
    }
}
