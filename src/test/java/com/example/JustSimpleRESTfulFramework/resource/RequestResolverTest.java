package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.TestApp;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class RequestResolverTest {

    RequestResolver requestResolver;

    @BeforeAll
    void setUp() {
        requestResolver = new RequestResolver(TestApp.class);
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_GET() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test_get");
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and__http_method_is_POST() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test_post");
    }

    @Test
    void should_get_NOT_FOUND_when_url_of_request_is_not_matched() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/not-match-path");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.NOT_FOUND);
        assertEquals(result.getResult(), "Not Found");
    }
}
