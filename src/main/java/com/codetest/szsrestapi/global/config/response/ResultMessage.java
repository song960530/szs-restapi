package com.codetest.szsrestapi.global.config.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Data
@Builder
@ApiModel(description = "Api 정상 응답 공통 모델")
public class ResultMessage {
    @ApiModelProperty(name = "status", value = "상태코드(숫자)")
    private int status;
    @ApiModelProperty(name = "message", value = "메세지")
    private String message;
    @ApiModelProperty(name = "data", value = "응답 데이터")
    private Object data;

    public final static ResultMessage of(final Object data, final HttpStatus status) {
        return ResultMessage.builder()
                .data(data)
                .message("")
                .status(status.value())
                .build();
    }

    public final static ResultMessage of(final String message, final HttpStatus status) {
        return ResultMessage.builder()
                .data(new HashMap<String, String>())
                .message(message)
                .status(status.value())
                .build();
    }

    public final static ResultMessage of(final Object data, final String message, final HttpStatus status) {
        return ResultMessage.builder()
                .data(data)
                .message(message)
                .status(status.value())
                .build();
    }
}
