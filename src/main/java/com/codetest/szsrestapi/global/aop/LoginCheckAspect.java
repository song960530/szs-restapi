package com.codetest.szsrestapi.global.aop;

import com.codetest.szsrestapi.api.entity.UserIp;
import com.codetest.szsrestapi.api.service.UserService;
import com.codetest.szsrestapi.global.config.security.adapter.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public void loginCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String clientIp = userService.findClientIp(request);
        UserIp userIp = ((UserAdapter) authentication.getPrincipal()).getUser().getUserIp();

        // 로그인시 저장된 IP와 요청 IP가 다를경우 동작
        if (userIp == null || !userIp.getLoginIp().equals(clientIp))
            throw new IllegalStateException("재로그인 해주세요");
    }
}
