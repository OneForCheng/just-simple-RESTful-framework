package com.example.JustSimpleRESTfulFramework.model;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequestParam {
    private String path;
    private Map<String, List<String>> parameters;
    private HttpMethod method;
}
