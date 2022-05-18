package com.codetest.szsrestapi.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Scrap002Dto {

    @JsonProperty("총사용금액")
    private String useAmount;

    @JsonProperty("소득구분")
    private String type;
}
