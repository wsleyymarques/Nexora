package com.nexora.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        Map<String, String> errors,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, null, path, LocalDateTime.now());
    }

    public static ErrorResponse validation(int status, String error,
                                           Map<String, String> errors, String path) {
        return new ErrorResponse(status, error, null, errors, path, LocalDateTime.now());
    }
}