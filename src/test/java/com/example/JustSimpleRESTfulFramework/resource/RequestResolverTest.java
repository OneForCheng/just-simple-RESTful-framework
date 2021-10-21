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
    void should_get_OK_response_when_url_of_request_is_matched() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test");
    }
}
