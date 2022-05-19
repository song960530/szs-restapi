package com.codetest.szsrestapi.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResDto {
    private String token;
    private String type;
}
