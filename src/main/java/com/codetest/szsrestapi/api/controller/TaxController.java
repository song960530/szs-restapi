package com.codetest.szsrestapi.api.controller;

import com.codetest.szsrestapi.api.entity.ScrapHistory;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.exception.ScrapApiException;
import com.codetest.szsrestapi.api.service.TaxService;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TaxController {
    private final TaxService taxService;

    @ApiOperation(
            value = "삼쩜삼 스크랩 API 호출"
            , notes = "삼쩜삼 스크랩 API를 호출하여 환급액 계산에 필요한 정보를 저장합니다\n" +
            "로그인 API 응답값을 참고하여 우측 상단 또는 우측 좌물쇠버튼을 클릭,\n" +
            "Value 입력란 안에 \"BEARER [토큰값]\"을 입력하여 호출"
    )
    @PostMapping("/szs/scrap")
    @LoginCheck
    public ResponseEntity<ResultMessage> scrap(@ApiIgnore @LoginUser User user) {

        ResponseEntity<Object> apiResponse = taxService.callScrapApi(user);
        ScrapHistory scrapHistory = taxService.recordScrapHistory(user, apiResponse);

        if (!apiResponse.getStatusCode().is2xxSuccessful())
            throw new ScrapApiException("알수없는 이유로 스크랩통신을 실패하였습니다");

        return new ResponseEntity<>(
                ResultMessage.of(taxService.scrap(user, apiResponse, scrapHistory), "동기스크랩", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @ApiOperation(
            value = "환급액 계산 API"
            , notes = "스크랩 정보를 바탕으로 유저의 환급액을 계산합니다\n" +
            "로그인 API 응답값을 참고하여 우측 상단 또는 우측 좌물쇠버튼을 클릭,\n" +
            "Value 입력란 안에 \"BEARER [토큰값]\"을 입력하여 호출"
    )
    @GetMapping("/szs/refund")
    @LoginCheck
    public ResponseEntity<ResultMessage> refund(@ApiIgnore @LoginUser User user) {

        return new ResponseEntity<>(
                ResultMessage.of(taxService.refund(user), "환급액", HttpStatus.OK)
                , HttpStatus.OK
        );
    }
}
