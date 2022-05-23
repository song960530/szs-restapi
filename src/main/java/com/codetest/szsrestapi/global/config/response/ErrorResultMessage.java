package com.codetest.szsrestapi.global.config.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Data
@Builder
@ApiModel(description = "Api 에러 응답 공통 모델")
public class ErrorResultMessage {
    @ApiModelProperty(name = "status", value = "상태코드(숫자)")
    private int status;
    @ApiModelProperty(name = "message", value = "메세지")
    private String message;
    @ApiModelProperty(name = "error", value = "에러 데이터")
    private Object error;

    public final static ErrorResultMessage of(final Object error, final HttpStatus status) {
        return ErrorResultMessage.builder()
                .error(error)
                .message("")
                .status(status.value())
                .build();
    }

    public final static ErrorResultMessage of(final String message, final HttpStatus status) {
        return ErrorResultMessage.builder()
                .error(new HashMap<String, String>())
                .message(message)
                .status(status.value())
                .build();
    }

    public final static ErrorResultMessage of(final Object error, final String message, final HttpStatus status) {
        return ErrorResultMessage.builder()
                .error(error)
                .message(message)
                .status(status.value())
                .build();
    }
}
