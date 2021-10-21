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

import java.util.List;

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
    void should_get_NOT_FOUND_when_url_of_request_is_not_matched() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/not-match-path");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.NOT_FOUND);
        assertEquals(result.getResult(), "Not Found");
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_GET() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test_get");
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_POST() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test_post");
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_with_sub_resource() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/sub-resource/get");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "test_sub_resource");
    }

    @Test
    void should_get_string_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/string-query-param?name=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "hello");
    }

    @Test
    void should_get_integer_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/integer-query-param?count=10");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), 10);
    }

    @Test
    void should_get_array_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/array-query-param?tags=1&tags=abc&tags=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        String[] tags = (String[])result.getResult();
        assertEquals(tags.length, 3);
        assertEquals(tags[0], "1");
        assertEquals(tags[1], "abc");
        assertEquals(tags[2], "hello");
    }

    @Test
    void should_get_list_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/list-query-param?tags=1&tags=abc&tags=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        List<String> tags = (List<String>)result.getResult();
        assertEquals(tags.size(), 3);
        assertEquals(tags.get(0), "1");
        assertEquals(tags.get(1), "abc");
        assertEquals(tags.get(2), "hello");
    }

    @Test
    void should_get_path_param_when_url_of_request_is_matched_and_with_path_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/path-param/123?id=456");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(result.getStatus(), HttpResponseStatus.OK);
        assertEquals(result.getResult(), "123");
    }
}
