package com.codetest.szsrestapi.domain.user.controller;

import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.LoginReqDto;
import com.codetest.szsrestapi.domain.user.service.UserService;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService memberService;

    @PostMapping("/szs/signup")
    public ResponseEntity<ResultMessage> signup(@Valid @RequestBody JoinReqDto requestDto) {
        return new ResponseEntity<>(memberService.signup(requestDto), HttpStatus.OK);
    }

    @PostMapping("/szs/login")
    public ResponseEntity<ResultMessage> login(@Valid @RequestBody LoginReqDto requestDto) {
        return new ResponseEntity<>(memberService.login(requestDto), HttpStatus.OK);
    }

    @GetMapping("/szs/me")
    public ResponseEntity<ResultMessage> whoAmI() {
        return new ResponseEntity<>(memberService.whoAmI(), HttpStatus.OK);
    }
}
