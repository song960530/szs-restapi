package com.codetest.szsrestapi.global.aop;

import com.codetest.szsrestapi.api.entity.UserIp;
import com.codetest.szsrestapi.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
class LoginCheckAspectTest {
    private LoginCheckAspect loginCheckAspect;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        loginCheckAspect = new LoginCheckAspect(userService);
    }


    @Test
    @DisplayName("SecurityContext에 인증정보가 없을 때 예외처리")
    public void NoAuthInSecurityContext() throws Exception {
        // given

        // when

        // then
        assertThrows(IllegalStateException.class, () -> {
            loginCheckAspect.loginCheck();
        });
    }

    @Test
    @DisplayName("로그인IP와 요청IP가 다를경우 예외처리_null일때")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void NoMatchClientAndLoginIp() throws Exception {
        // given

        // when
        doReturn("127.0.0.1").when(userService).findClientIp(any(HttpServletRequest.class));
        doReturn(null).when(userService).findUserLoginIp(anyString());

        // then
        assertThrows(IllegalStateException.class, () -> {
            loginCheckAspect.loginCheck();
        });
    }

    @Test
    @DisplayName("로그인IP와 요청IP가 다를경우 예외처리_다른값일때")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void NoMatchClientAndLoginIp2() throws Exception {
        // given
        UserIp userIp = mock(UserIp.class);
        when(userIp.getLoginIp()).thenReturn("0.0.0.1");

        // when
        doReturn("127.0.0.1").when(userService).findClientIp(any(HttpServletRequest.class));
        doReturn(userIp).when(userService).findUserLoginIp(anyString());

        // then
        assertThrows(IllegalStateException.class, () -> {
            loginCheckAspect.loginCheck();
        });
    }

}