package com.codetest.szsrestapi.global.error;

import com.codetest.szsrestapi.domain.user.exception.UserException;
import com.codetest.szsrestapi.global.config.response.ErrorResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResultMessage> exception(Exception e) {
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(ErrorResultMessage.of(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResultMessage> exception(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(ErrorResultMessage.of(errors, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResultMessage> illegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(ErrorResultMessage.of(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResultMessage> illegalStateException(IllegalStateException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(ErrorResultMessage.of(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResultMessage> userException(UserException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(ErrorResultMessage.of(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CipherException.class)
    public ResponseEntity<ErrorResultMessage> cipherException(CipherException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(ErrorResultMessage.of(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
