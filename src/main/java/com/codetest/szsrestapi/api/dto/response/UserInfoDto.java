package com.codetest.szsrestapi.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {
    private Long userNo;
    private String userId;
    private String name;
    private String regNo;
}
