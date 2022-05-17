package com.codetest.szsrestapi.domain.user.dto.request;

import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.entity.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

@Data
public class JoinReqDto {

    @NotEmpty(message = "Please enter your userId")
    @Size(min = 4, message = "userId should have at least 4 characters")
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 4, message = "password should have at least 4 characters")
    private String password;

    @NotEmpty(message = "Please enter your name")
    @Size(min = 2, message = "name should have at least 2 characters")
    private String name;

    @NotEmpty(message = "Please enter your regNo(ID card number)")
    @Size(min = 14, message = "regNo(ID card number) should have at least 14 characters")
    private String regNo;

    private Role role;

    private String encPassword;
    private String encRegNo;

    public User toEntity() {
        return new User(userId, encPassword, name, encRegNo, Arrays.asList(role));
    }
}
