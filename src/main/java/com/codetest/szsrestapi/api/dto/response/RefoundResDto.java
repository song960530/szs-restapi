package com.codetest.szsrestapi.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefoundResDto {
    @JsonProperty("이름")
    private String name;

    @JsonProperty("한도")
    private String limit;

    @JsonProperty("공제액")
    private String deductedAmount;

    @JsonProperty("환급액")
    private String refundAmount;
}
