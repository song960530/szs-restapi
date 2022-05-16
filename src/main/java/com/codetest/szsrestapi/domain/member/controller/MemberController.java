package com.codetest.szsrestapi.domain.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MemberController {
    @GetMapping("/hello")
    public String hello() {
        return "ok";
    }
}
