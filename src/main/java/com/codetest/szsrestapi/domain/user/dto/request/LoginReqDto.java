package com.codetest.szsrestapi.domain.user.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LoginReqDto {
    @NotEmpty(message = "Please enter your userId")
    @Size(min = 1, message = "userId should have at least 1 characters")
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 1, message = "password should have at least 1 characters")
    private String password;
}
