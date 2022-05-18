package com.codetest.szsrestapi.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefoundResDto {
    private String name;
    private String limit;
    private String deductedAmount;
    private String refundAmount;
}
