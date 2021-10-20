package com.example.JustSimpleRESTfulFramework.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequestParam {
    private String path;
    private Map<String, List<String>> queryParameters;
    private HttpMethod method;
    private ByteBuf body;
}
