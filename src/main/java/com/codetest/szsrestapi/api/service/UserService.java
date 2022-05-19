package com.codetest.szsrestapi.api.service;

import com.codetest.szsrestapi.api.dto.request.JoinReqDto;
import com.codetest.szsrestapi.api.dto.request.LoginReqDto;
import com.codetest.szsrestapi.api.dto.response.LoginResDto;
import com.codetest.szsrestapi.api.dto.response.UserInfoDto;
import com.codetest.szsrestapi.api.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    public User signup(JoinReqDto requestDto);

    public LoginResDto login(LoginReqDto requestDto, HttpServletRequest request);

    public UserInfoDto whoAmI();
}
