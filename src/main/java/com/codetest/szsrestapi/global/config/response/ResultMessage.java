package com.codetest.szsrestapi.global.config.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Data
@Builder
public class ResultMessage {
    private int status;
    private String message;
    private Object data;

    public final static ResultMessage of(final Object data, final HttpStatus status) {
        return ResultMessage.builder()
                .data(data)
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
