package com.codetest.szsrestapi.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Scrap001Dto {

    @JsonProperty("소득내역")
    private String type;

    @JsonProperty("총지급액")
    private String salary;

    @JsonProperty("업무시작일")
    private String startDt;

    @JsonProperty("기업명")
    private String businessName;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("지급일")
    private String paymentDt;

    @JsonProperty("업무종료일")
    private String finishDt;

    @JsonProperty("주민등록번호")
    private String regNo;

    @JsonProperty("소득구분")
    private String type2;

    @JsonProperty("사업자등록번호")
    private String businessRegNo;
}
