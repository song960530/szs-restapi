package com.codetest.szsrestapi.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapReqDto {
    private String name;
    private String regNo;
}
