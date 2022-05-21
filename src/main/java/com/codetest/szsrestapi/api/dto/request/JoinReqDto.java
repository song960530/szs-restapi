package com.codetest.szsrestapi.api.dto.request;

import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.api.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

@Data
@ApiModel(description = "회원가입 요청 DTO")
public class JoinReqDto {

    @NotEmpty(message = "Please enter your userId")
    @Size(min = 4, message = "userId should have at least 4 characters")
    @ApiModelProperty(name = "userId", value = "사용자 ID", example = "hong12", required = true)
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 4, message = "password should have at least 4 characters")
    @ApiModelProperty(name = "password", value = "사용자 PW", example = "123456", required = true)
    private String password;

    @NotEmpty(message = "Please enter your name")
    @Size(min = 2, message = "name should have at least 2 characters")
    @ApiModelProperty(name = "name", value = "사용자 이름", example = "홍길동", required = true)
    private String name;

    @NotEmpty(message = "Please enter your regNo(ID card number)")
    @Size(min = 14, message = "regNo(ID card number) should have at least 14 characters")
    @ApiModelProperty(name = "regNo", value = "사용자 주민번호", example = "860824-1655068", required = true)
    private String regNo;

    @ApiModelProperty(hidden = true)
    private Role role;

    @ApiModelProperty(hidden = true)
    private String encPassword;

    @ApiModelProperty(hidden = true)
    private String encRegNo;

    public User toEntity() {
        return new User(userId, encPassword, name, encRegNo, Arrays.asList(role));
    }

    public JoinReqDto() {
    }

    public JoinReqDto(String userId, String password, String name, String regNo) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
    }
}
