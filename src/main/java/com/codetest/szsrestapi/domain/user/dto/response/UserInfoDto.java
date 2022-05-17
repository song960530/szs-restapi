package com.codetest.szsrestapi.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private Long userNo;
    private String userId;
    private String name;
    private String regNo;

    public static UserInfoDto creatDto(Long userNo, String userId, String name, String regNo) {
        return new UserInfoDto(userNo, userId, name, regNo);
    }
}
