package com.codetest.szsrestapi.api.controller;

import com.codetest.szsrestapi.api.dto.request.JoinReqDto;
import com.codetest.szsrestapi.api.dto.request.LoginReqDto;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.service.UserService;
import com.codetest.szsrestapi.global.annotation.LoginCheck;
import com.codetest.szsrestapi.global.annotation.LoginUser;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService memberService;

    @ApiOperation(
            value = "회원가입 API"
            , notes = "아래 회원만 가입이 가능하며, 동일ID, 동일 주민번호 중복가입은 불가능합니다\n" +
            "1. 홍길동, 860824-1655068\n" +
            "2. 김둘리, 921108-1582816\n" +
            "3. 마징가, 880601-2455116\n" +
            "4. 베지터, 910411-1656116\n" +
            "5. 손오공, 820326-2715702"
    )
    @PostMapping("/szs/signup")
    public ResponseEntity<ResultMessage> signup(@Valid @RequestBody JoinReqDto requestDto) {
        memberService.signup(requestDto);

        return new ResponseEntity<>(
                ResultMessage.of("회원가입", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @ApiOperation(
            value = "로그인 API"
            , notes = "로그인 성공 시 Authentication 헤더에 지정할 JWT값을 반환합니다"
    )
    @PostMapping("/szs/login")
    public ResponseEntity<ResultMessage> login(@Valid @RequestBody LoginReqDto requestDto, @ApiIgnore HttpServletRequest request) {

        return new ResponseEntity<>(
                ResultMessage.of(memberService.login(requestDto, request), "로그인", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @ApiOperation(
            value = "내 정보 API"
            , notes = "내 정보를 조회합니다\n" +
            "로그인 API 응답값을 참고하여 우측 상단 또는 우측 좌물쇠버튼을 클릭,\n" +
            "Value 입력란 안에 \"BEARER [토큰값]\"을 입력하여 호출"
    )
    @GetMapping("/szs/me")
    @LoginCheck
    public ResponseEntity<ResultMessage> whoAmI(@LoginUser User user) {

        return new ResponseEntity<>(
                ResultMessage.of(memberService.whoAmI(user), "내정보", HttpStatus.OK)
                , HttpStatus.OK
        );
    }
}
