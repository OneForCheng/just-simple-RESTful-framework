package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.resource.ParamResolver;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResourceNode {
    private String url;
    private HttpMethod httpMethod;
    private Method method;

    @SneakyThrows
    public Object invoke(Object resourceInstance, RequestEntity requestEntity) {
        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(url, requestEntity.getPath());
        Object[] arguments = ParamResolver.getParameterInstances(method, requestEntity, pathParameters);
        return method.invoke(resourceInstance, arguments);
    }
}
