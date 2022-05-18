package com.codetest.szsrestapi.domain.user.service;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.LoginReqDto;
import com.codetest.szsrestapi.domain.user.dto.request.ScrapReqDto;
import com.codetest.szsrestapi.domain.user.dto.response.LoginResDto;
import com.codetest.szsrestapi.domain.user.dto.response.UserInfoDto;
import com.codetest.szsrestapi.domain.user.entity.Scrap;
import com.codetest.szsrestapi.domain.user.entity.ScrapHistory;
import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.exception.ScrapApiException;
import com.codetest.szsrestapi.domain.user.exception.UserException;
import com.codetest.szsrestapi.domain.user.repository.RoleRepository;
import com.codetest.szsrestapi.domain.user.repository.ScrapHistoryRepository;
import com.codetest.szsrestapi.domain.user.repository.ScrapRepository;
import com.codetest.szsrestapi.domain.user.repository.UserRepository;
import com.codetest.szsrestapi.global.annotation.LoginCheck;
import com.codetest.szsrestapi.global.config.jwt.JwtTokenProvider;
import com.codetest.szsrestapi.global.config.properties.SzsScrapProperties;
import com.codetest.szsrestapi.global.util.cipher.AES256Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.LinkedHashMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final AES256Util aes256Util;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;
    private final SzsScrapProperties scrapProperties;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ScrapRepository scrapRepository;
    private final ScrapHistoryRepository scrapHistoryRepository;

    @Transactional
    public void signup(JoinReqDto requestDto) {
        requestDto.setRole(roleRepository.findByRoles(EnumRole.ROLE_USER).orElseThrow(
                () -> new UserException("권한 정보를 찾을 수 없습니다")
        ));
        requestDto.setEncPassword(passwordEncoder.encode(requestDto.getPassword()));
        requestDto.setEncRegNo(aes256Util.encrypt(requestDto.getRegNo()));

        userRepository.save(requestDto.toEntity());
    }

    public LoginResDto login(LoginReqDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 ID입니다")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("패스워드가 맞지 않습니다");

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getRoles());

        return new LoginResDto(token, "BEARER");
    }

    @LoginCheck
    public UserInfoDto whoAmI() {
        User user = findUserIdFromAuth();

        return UserInfoDto.creatDto(user.getUserNo(), user.getUserId(), user.getName(), aes256Util.decrypt(user.getRegNo()));
    }


    @Transactional
    public Object scrap() throws ScrapApiException {
        User user = findUserIdFromAuth();

        ResponseEntity<Object> apiResponse = connScrapApi(user);
        LinkedHashMap body = (LinkedHashMap) apiResponse.getBody();

        ScrapHistory scrapHistory = recordScrapHistory(user, apiResponse);

        if (apiResponse.getStatusCode() != HttpStatus.OK)
            throw new ScrapApiException("스크랩 API와 통신에 실패하였습니다");

        if (body.get("status").equals("fail"))
            throw new ScrapApiException("조회된 정보가 없습니다");


        JSONObject object = new JSONObject(apiResponse).getJSONObject("body").getJSONObject("data").getJSONObject("jsonList");
        JSONObject scrap001 = object.getJSONArray("scrap001").getJSONObject(0);
        JSONObject scrap002 = object.getJSONArray("scrap002").getJSONObject(0);

        int salary = Integer.parseInt(scrap001.getString("총지급액").replaceAll(",", ""));
        int useAmount = Integer.parseInt(scrap002.getString("총사용금액").replaceAll(",", ""));

        Scrap scrap = new Scrap(user, salary, useAmount, scrapHistory);
        scrapRepository.save(scrap);

        return body.get("data");
    }

    private ResponseEntity<Object> connScrapApi(User user) {
        return restTemplate.postForEntity(
                URI.create(scrapProperties.getScrapUrl())
                , new ScrapReqDto(user.getName(), aes256Util.decrypt(user.getRegNo()))
                , Object.class
        );
    }

    @Transactional
    public ScrapHistory recordScrapHistory(User user, ResponseEntity<Object> apiResponse) {
        ScrapHistory scrapHistory = new ScrapHistory(user, apiResponse.getStatusCode().toString(), apiResponse.getBody().toString());

        return scrapHistoryRepository.save(scrapHistory);
    }

    public User findUserIdFromAuth() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다"));
    }
}
