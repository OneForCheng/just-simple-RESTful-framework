package com.example.JustSimpleRESTfulFramework.model;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseResult {
    private HttpResponseStatus status;
    private Object result;
}
