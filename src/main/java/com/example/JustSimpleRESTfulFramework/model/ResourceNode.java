package com.example.JustSimpleRESTfulFramework.model;

import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class ResourceNode {
    private String url;
    private HttpMethod httpMethod;
    private Method method;
}
