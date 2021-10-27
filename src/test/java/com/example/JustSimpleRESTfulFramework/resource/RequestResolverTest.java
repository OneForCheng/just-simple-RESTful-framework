package com.example.JustSimpleRESTfulFramework.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.JustSimpleRESTfulFramework.TestApp;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.model.TestRequestBody;
import io.netty.buffer.Unpooled;
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
        assertEquals(HttpResponseStatus.NOT_FOUND, result.getStatus());
        assertEquals("Not Found", result.getResult());
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_GET() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("test_get", result.getResult());
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_and_http_method_is_POST() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/test");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("test_post", result.getResult());
    }

    @Test
    void should_get_OK_when_url_of_request_is_matched_with_sub_resource() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/sub-resource/get");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("test_sub_resource", result.getResult());
    }

    @Test
    void should_get_string_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/string-query-param?name=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("hello", result.getResult());
    }

    @Test
    void should_get_integer_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/integer-query-param?count=10");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals(10, result.getResult());
    }

    @Test
    void should_get_array_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/array-query-param?tags=1&tags=abc&tags=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        String[] tags = (String[])result.getResult();
        assertEquals(3, tags.length);
        assertEquals("1", tags[0]);
        assertEquals("abc", tags[1]);
        assertEquals("hello", tags[2]);
    }

    @Test
    void should_get_list_query_param_when_url_of_request_is_matched_and_with_query_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/list-query-param?tags=1&tags=abc&tags=hello");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        List<String> tags = (List<String>)result.getResult();
        assertEquals(3, tags.size());
        assertEquals("1", tags.get(0));
        assertEquals("abc", tags.get(1));
        assertEquals("hello", tags.get(2));
    }

    @Test
    void should_get_path_param_when_url_of_request_is_matched_and_with_path_param() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test/path-param/123?id=456");
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("123", result.getResult());
    }

    @Test
    void should_get_request_body_when_url_of_request_is_matched_and_with_request_body() {
        TestRequestBody body = new TestRequestBody("123", "test", 10.5);
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/test/request-body", Unpooled.wrappedBuffer(bytes));
        ResponseResult result = requestResolver.resolve(httpRequest);
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        TestRequestBody data = (TestRequestBody)result.getResult();
        assertEquals(body.getId(), data.getId());
        assertEquals(body.getName(), data.getName());
        assertEquals(body.getAge(), 0.0001, data.getAge());
    }
}
