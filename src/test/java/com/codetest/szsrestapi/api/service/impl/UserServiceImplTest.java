package com.codetest.szsrestapi.api.service.impl;

import com.codetest.szsrestapi.api.dto.request.JoinReqDto;
import com.codetest.szsrestapi.api.dto.request.LoginReqDto;
import com.codetest.szsrestapi.api.dto.response.LoginResDto;
import com.codetest.szsrestapi.api.dto.response.UserInfoDto;
import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.entity.UserIp;
import com.codetest.szsrestapi.api.enums.EnumRole;
import com.codetest.szsrestapi.api.exception.UserException;
import com.codetest.szsrestapi.api.repository.RoleRepository;
import com.codetest.szsrestapi.api.repository.UserIpRepository;
import com.codetest.szsrestapi.api.repository.UserRepository;
import com.codetest.szsrestapi.global.config.jwt.JwtTokenProvider;
import com.codetest.szsrestapi.global.config.properties.AES256Properties;
import com.codetest.szsrestapi.global.config.properties.JwtProperties;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
//@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private JwtTokenProvider jwtTokenProvider;
    private BCryptPasswordEncoder passwordEncoder;
    private UserServiceImpl userService;
    private AES256Util aes256Util;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserIpRepository userIpRepository;
    @Mock
    private UserDetailsService userDetailsService;


    @BeforeEach
    void setUp() {
        aes256Util = new AES256Util(new AES256Properties("szsrestapisecretszsrestapisecret", "szsrestapisecret"));
        aes256Util.init();
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, new JwtProperties("szs-apiSecretKey", 1800000));
        jwtTokenProvider.init();
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(aes256Util, jwtTokenProvider, passwordEncoder, userRepository, roleRepository, userIpRepository);
    }

    private JoinReqDto joinReqDto() {
        return new JoinReqDto("test1", "123456", "홍길동", "860824-1655068");

    }

    @Test
    @DisplayName("권한 정보를 찾을 수 없음(ROLE 정보가 없을때)")
    public void noRole() throws Exception {
        // given
        JoinReqDto joinReqDto = joinReqDto();

        // when
//        doThrow(UserException.class).when(roleRepository).findByRoles(any(EnumRole.class));
        doReturn(Optional.empty()).when(roleRepository).findByRoles(any(EnumRole.class));

        // then
        assertThrows(UserException.class, () -> {
            userService.signup(joinReqDto);
        });
    }

    @Test
    @DisplayName("아이디가 중복되었을 때 예외처리")
    public void overlapId() throws Exception {
        // given
        JoinReqDto joinReqDto = joinReqDto();
        Role role = mock(Role.class);

        // when
        doReturn(Optional.of(role)).when(roleRepository).findByRoles(any(EnumRole.class));
//        doThrow(IllegalStateException.class).when(userRepository).existsByUserId(anyString());
        doReturn(true).when(userRepository).existsByUserId(anyString());

        // then
        assertThrows(IllegalStateException.class, () -> {
            userService.signup(joinReqDto);
        });
    }

    @Test
    @DisplayName("이미 가입된 중복 회원 예외처리")
    public void overlapRegNo() throws Exception {
        // given
        JoinReqDto joinReqDto = joinReqDto();
        Role role = mock(Role.class);

        // when
        doReturn(Optional.of(role)).when(roleRepository).findByRoles(any(EnumRole.class));
        doReturn(false).when(userRepository).existsByUserId(anyString());
//        doThrow(IllegalStateException.class).when(userRepository).existsByRegNo(anyString());
        doReturn(true).when(userRepository).existsByRegNo(anyString());

        // then
        assertThrows(IllegalStateException.class, () -> {
            userService.signup(joinReqDto);
        });
    }

    @Test
    @DisplayName("회원가입 성공")
    public void singupSuccess() throws Exception {
        // given
        JoinReqDto joinReqDto = joinReqDto();
        Role role = mock(Role.class);
        User user = mock(User.class);

        // when
        doReturn(Optional.of(role)).when(roleRepository).findByRoles(any(EnumRole.class));
        doReturn(false).when(userRepository).existsByUserId(anyString());
        doReturn(false).when(userRepository).existsByRegNo(anyString());
        doReturn(user).when(userRepository).save(any(User.class));

        // then
        assertEquals(user, userService.signup(joinReqDto));
    }

    @Test
    @DisplayName("특정회원만 가입되는지 확인")
    public void limitSignupChk() throws Exception {
        // given
        JoinReqDto joinReqDto = new JoinReqDto("test1", "123123123", "이거안됨", "주민번호몰랑");

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.limitSingup(joinReqDto);
        });
    }

    @Test
    @DisplayName("로그인시 아이디 없을때 예외처리")
    public void notFoundUserId() throws Exception {
        // given
        LoginReqDto mockDto = mock(LoginReqDto.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(mockDto.getUserId()).thenReturn("test");

        // when
//        doThrow(IllegalArgumentException.class).when(userRepository).findByUserId(anyString());
        doReturn(Optional.empty()).when(userRepository).findByUserId(anyString());

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(mockDto, request);
        });
    }

    @Test
    @DisplayName("패스워드 안맞을떄 예외처리")
    public void notEqualsPw() throws Exception {
        // given
        User userMock = mock(User.class);
        LoginReqDto dtoMock = mock(LoginReqDto.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(dtoMock.getUserId()).thenReturn("Id");
        when(dtoMock.getPassword()).thenReturn("Pw222222");
        when(userMock.getPassword()).thenReturn(passwordEncoder.encode("Pw"));

        // when
        doReturn(Optional.of(userMock)).when(userRepository).findByUserId(anyString());

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(dtoMock, request);
        });
    }

    @Test
    @DisplayName("로그인 정상처리")
    public void loginSuccess() throws Exception {
        // given
        User userMock = mock(User.class);
        LoginReqDto dtoMock = mock(LoginReqDto.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(dtoMock.getUserId()).thenReturn("Id");
        when(dtoMock.getPassword()).thenReturn("Pw");
        when(userMock.getPassword()).thenReturn(passwordEncoder.encode("Pw"));
        when(userMock.getRoles()).thenReturn(Arrays.asList(new Role(EnumRole.ROLE_USER)));

        // when
        doReturn(Optional.of(userMock)).when(userRepository).findByUserId(anyString());
        doReturn(mock(UserIp.class)).when(userIpRepository).save(any());

        // then
        LoginResDto resDto = userService.login(dtoMock, request);
        assertNotNull(resDto.getToken());
        assertEquals("BEARER", resDto.getType());
    }

    @Test
    @DisplayName("헤더에서 클라이언트IP 뽑아내기(X-FORWARDED-FOR)")
    public void findClientIP() throws Exception {
        // given
        String ip = "0.0.0.1";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader("X-FORWARDED-FOR")).thenReturn(ip);

        // when

        // then
        String result = userService.findClientIp(requestMock);
        assertEquals(ip, result);
    }

    @Test
    @DisplayName("헤더에서 클라이언트IP 뽑아내기(getRemoteAddr)")
    public void findClientIP2() throws Exception {
        // given
        String ip = "0.0.0.1";
        String ip2 = "0.0.0.2";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader("X-FORWARDED-FOR")).thenReturn(null);
        when(requestMock.getRemoteAddr()).thenReturn(ip2);

        // when

        // then
        String result = userService.findClientIp(requestMock);
        assertEquals(ip2, result);
        assertNotEquals(ip, result);
    }

    @Test
    @DisplayName("로그인시 기존 로그인 IP가 있으면 update")
    public void updateWhenIpExist() throws Exception {
        // given
        String ip = "0.0.0.1";
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader("X-FORWARDED-FOR")).thenReturn(ip);

        UserIp userIpMock = mock(UserIp.class);
        doNothing().when(userIpMock).changeLoginIp(anyString());

        User userMock = mock(User.class);
        when(userMock.getUserId()).thenReturn("test");

        // when
        doReturn(Optional.of(userIpMock)).when(userIpRepository).findByUser_UserId(anyString());

        // then
        userService.recordUserIp(requestMock, userMock);
    }

    @Test
    @DisplayName("SecurityContext에서 Authentication 가져오기")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void getAuthenticationFromSecurityContext() throws Exception {
        // given
        User userMock = mock(User.class);

        // when
        doReturn(Optional.of(userMock)).when(userRepository).findByUserId(anyString());

        // then
        User user = userService.findUserIdFromAuth();
        assertNotNull(user);
        assertEquals(userMock, user);
    }

    @Test
    @DisplayName("SecurityContext에서 Authentication ID로 조회안됐을때")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void notFoundUser() throws Exception {
        // given
        User userMock = mock(User.class);

        // when
        doReturn(Optional.empty()).when(userRepository).findByUserId(anyString());

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findUserIdFromAuth();
        });
    }

    @Test
    @DisplayName("내정보조회")
    @WithMockUser(username = "test1", password = "123456", roles = {"USER"})
    public void whoAmI() throws Exception {
        // given
        String encrypt = aes256Util.encrypt("860824-1655068");

        User userMock = mock(User.class);
        when(userMock.getUserNo()).thenReturn(1L);
        when(userMock.getUserId()).thenReturn("test1");
        when(userMock.getName()).thenReturn("홍길동");
        when(userMock.getRegNo()).thenReturn(encrypt);

        // when
        doReturn(Optional.of(userMock)).when(userRepository).findByUserId(anyString());

        // then
        UserInfoDto result = userService.whoAmI();
        assertNotNull(result);
        assertEquals("홍길동", result.getName());
        assertEquals("test1", result.getUserId());
        assertEquals(aes256Util.decrypt(encrypt), result.getRegNo());
    }
}