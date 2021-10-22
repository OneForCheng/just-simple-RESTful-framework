package com.example.JustSimpleRESTfulFramework.model;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceUrlAndMethod {
    private String url;
    private HttpMethod method;
}
