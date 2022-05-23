package com.codetest.szsrestapi.global.config.jwt;

import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.api.enums.EnumRole;
import com.codetest.szsrestapi.global.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("dev")
@SpringBootTest
//@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    private JwtProperties jwtProperties;
    String encKey;

    @BeforeEach
    void setup() {
        jwtProperties = new JwtProperties("szs-apiSecretKey", 1800000);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtProperties);
        encKey = "c3pzLWFwaVNlY3JldEtleQ==";
        jwtTokenProvider.init();
    }

    public String getToken(String userId) {
        return jwtTokenProvider.createToken(userId, Arrays.asList(new Role(EnumRole.ROLE_USER)));
    }

    @Test
    @DisplayName("토큰 생성 확인")
    public void test() throws Exception {
        // given
        String userId = "test1";

        // when

        // then
        Claims claims = Jwts.parser()
                .setSigningKey(encKey)
                .parseClaimsJws(getToken(userId))
                .getBody();

        assertEquals(userId, claims.get("sub"));
        assertEquals("[" + EnumRole.ROLE_USER.toString() + "]", Arrays.asList(claims.get("roles")).get(0).toString());
    }

    @Test
    @DisplayName("회원 정보 확인")
    public void chkUserPk() throws Exception {
        // given
        String userId = "test1";

        // when

        // then
        String userPk = jwtTokenProvider.getUserPk(getToken(userId));
        assertNotNull(userPk);
        assertEquals(userId, userPk);
    }

    @Test
    @DisplayName("헤더에 토큰 값이 없을경우")
    public void noTokenInHeader() throws Exception {
        // given
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // when

        // then
        assertEquals("", jwtTokenProvider.resolveToken(request));
    }

    @Test
    @DisplayName("토큰값이 잘못되었을 경우 에러처리")
    public void notMatchedPattern() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("asdflakjsdfjlkjsdafklsdjf");

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.resolveToken(request);
        });
    }

    @Test
    @DisplayName("헤더에서 토큰값 정상적으로 가져왔을 때")
    public void successGetHeaderToken() throws Exception {
        // given
        String token = getToken("test1");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // when

        // then
        assertEquals(token, jwtTokenProvider.resolveToken(request));
    }

    @Test
    @DisplayName("토큰이 유효성 검사 정상 처리")
    public void successTokenValid() throws Exception {
        // given
        String token = getToken("test1");

        // when

        // then
        assertEquals(true, jwtTokenProvider.validToken(token));
    }
}