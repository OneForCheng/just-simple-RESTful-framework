package com.example.JustSimpleRESTfulFramework.exception;

public class HttpServerException extends RuntimeException {
    public HttpServerException(String message, Throwable e) {
        super(message, e);
    }

    public HttpServerException(String message) {
        super(message);
    }
}
