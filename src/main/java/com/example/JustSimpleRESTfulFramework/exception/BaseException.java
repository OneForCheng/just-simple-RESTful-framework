package com.example.JustSimpleRESTfulFramework.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private final String message;

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
    }
}
