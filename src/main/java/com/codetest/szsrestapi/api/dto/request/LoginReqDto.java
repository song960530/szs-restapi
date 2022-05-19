package com.codetest.szsrestapi.api.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@ApiModel(description = "로그인 요청 DTO")
public class LoginReqDto {
    @NotEmpty(message = "Please enter your userId")
    @Size(min = 1, message = "userId should have at least 1 characters")
    @ApiModelProperty(name = "userId", value = "사용자 ID", example = "hong12", required = true)
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 1, message = "password should have at least 1 characters")
    @ApiModelProperty(name = "password", value = "사용자 PW", example = "123456", required = true)
    private String password;
}
