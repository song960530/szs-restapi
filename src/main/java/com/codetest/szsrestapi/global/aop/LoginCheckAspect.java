package com.codetest.szsrestapi.global.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginCheckAspect {

    /**
     * @LoginCheck 어노테이션이 있으면 메소드 시작 전에 advice가 적용되도록 포인트컷 적용
     */
    @Before("@annotation(com.codetest.szsrestapi.global.annotation.LoginCheck)")
    public void loginCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            throw new IllegalStateException("로그인 정보가 없습니다");
        }
    }
}
