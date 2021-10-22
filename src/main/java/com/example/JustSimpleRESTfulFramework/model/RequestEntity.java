package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequestEntity {
    private String path;
    private Map<String, List<String>> queryParameters;
    private HttpMethod method;
    private ByteBuf body;

    public static RequestEntity of(FullHttpRequest request) {
        String uri = request.uri();
        String path  = UrlResolver.getBaseUrl(uri);
        Map<String, List<String>> parameters = UrlResolver.getUrlQueryParameters(uri);
        HttpMethod method = request.method();
        ByteBuf content = request.content();
        return new RequestEntity(path, parameters, method, content);
    }
}
