package com.codetest.szsrestapi.domain.user.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class LoginReqDto {
    @NotEmpty(message = "Please enter your userId")
    @Size(min = 1, message = "userId should have at least 1 characters")
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 1, message = "password should have at least 1 characters")
    private String password;
}
