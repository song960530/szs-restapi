package com.codetest.szsrestapi.global.aop;

import com.codetest.szsrestapi.domain.user.entity.UserIp;
import com.codetest.szsrestapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckAspect {
    private final UserService userService;

    /**
     * @LoginCheck 어노테이션이 있으면 메소드 시작 전에 advice가 적용되도록 포인트컷 적용
     */
    @Before("@annotation(com.codetest.szsrestapi.global.annotation.LoginCheck)")
    public void loginCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            throw new IllegalStateException("로그인 정보가 없습니다");
        }

        String clientIp = userService.findClientIp(request);
        UserIp userIp = userService.findUserLoginIp(authentication.getName());

        if (!userIp.getLoginIp().equals(clientIp))
            throw new IllegalStateException("재로그인 해주세요");
    }
}
