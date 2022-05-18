package com.codetest.szsrestapi.domain.user.controller;

import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.LoginReqDto;
import com.codetest.szsrestapi.domain.user.exception.ScrapApiException;
import com.codetest.szsrestapi.domain.user.service.UserService;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/szs")
public class UserController {
    private final UserService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ResultMessage> signup(@Valid @RequestBody JoinReqDto requestDto) {
        memberService.signup(requestDto);

        return new ResponseEntity<>(
                ResultMessage.of("회원가입", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResultMessage> login(@Valid @RequestBody LoginReqDto requestDto) {

        return new ResponseEntity<>(
                ResultMessage.of(memberService.login(requestDto), "로그인", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ResultMessage> whoAmI() {

        return new ResponseEntity<>(
                ResultMessage.of(memberService.whoAmI(), "내정보", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @PostMapping("/scrap")
    public ResponseEntity scrap() throws ScrapApiException {

        return new ResponseEntity<>(
                ResultMessage.of(memberService.scrap(), "동기스크랩", HttpStatus.OK)
                , HttpStatus.OK
        );
    }
}
