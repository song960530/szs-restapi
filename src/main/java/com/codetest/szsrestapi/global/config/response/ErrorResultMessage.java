package com.codetest.szsrestapi.global.config.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Data
@Builder
public class ErrorResultMessage {
    private int status;
    private String message;
    private Object error;

    public final static ErrorResultMessage of(final Object error, final HttpStatus status) {
        return ErrorResultMessage.builder()
                .error(error)
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
