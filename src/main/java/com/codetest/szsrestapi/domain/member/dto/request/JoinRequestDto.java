package com.codetest.szsrestapi.domain.member.dto.request;

import com.codetest.szsrestapi.domain.member.entity.Member;
import com.codetest.szsrestapi.domain.member.entity.Role;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

@Data
public class JoinRequestDto {

    @NotEmpty(message = "Please enter your userId")
    @Size(min = 1, message = "userId should have at least 1 characters")
    private String userId;

    @NotEmpty(message = "Please enter your password")
    @Size(min = 1, message = "password should have at least 1 characters")
    private String password;

    @NotEmpty(message = "Please enter your name")
    @Size(min = 1, message = "name should have at least 1 characters")
    private String name;

    @NotEmpty(message = "Please enter your regNo(ID card number)")
    @Size(min = 1, message = "regNo(ID card number) should have at least 1 characters")
    private String regNo;

    private Role role;

    private String encPassword;
    private String encRegNo;

    public Member toEntity() {
        return new Member(userId, encPassword, name, encRegNo, Arrays.asList(role));
    }
}
