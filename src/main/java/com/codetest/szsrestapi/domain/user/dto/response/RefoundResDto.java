package com.codetest.szsrestapi.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefoundResDto {
    private String name;
    private int limit;
    private int deductedAmount;
    private int refundAmount;
//    private String limit;
//    private String deductedAmount;
//    private String refundAmount;
}
