package com.codetest.szsrestapi.api.controller;

import com.codetest.szsrestapi.api.exception.ScrapApiException;
import com.codetest.szsrestapi.api.service.TaxService;
import com.codetest.szsrestapi.global.config.response.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TaxController {
    private final TaxService taxService;

    @PostMapping("/szs/scrap")
    public ResponseEntity scrap() throws ScrapApiException {

        return new ResponseEntity<>(
                ResultMessage.of(taxService.scrap(), "동기스크랩", HttpStatus.OK)
                , HttpStatus.OK
        );
    }

    @GetMapping("/szs/refund")
    public ResponseEntity refund() {

        return new ResponseEntity<>(
                ResultMessage.of(taxService.refund(), "환급액", HttpStatus.OK)
                , HttpStatus.OK
        );
    }
}
