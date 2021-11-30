package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.model.TestDemo;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.*;

class ResourceItemTest {
    @Test
    void should_return_false_if_url_is_not_matched() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode = new ResourceNode("/no-test", HttpMethod.GET, TestDemo.class.getMethod("nonResponse"));
        ResourceItem item = new ResourceItem(resourceNode);
        assertFalse(item.isMatch(RequestEntity.of(httpRequest)));
    }

    @Test
    void should_return_false_if_http_method_is_not_matched() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/test");
        ResourceNode resourceNode = new ResourceNode("/test", HttpMethod.GET, TestDemo.class.getMethod("nonResponse"));
        ResourceItem item = new ResourceItem(resourceNode);
        assertFalse(item.isMatch(RequestEntity.of(httpRequest)));
    }

    @Test
    void should_return_true_if_http_method_and_url_are_matched() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode = new ResourceNode("/test", HttpMethod.GET, TestDemo.class.getMethod("nonResponse"));
        ResourceItem item = new ResourceItem(resourceNode);
        assertTrue(item.isMatch(RequestEntity.of(httpRequest)));
    }

    @Test
    void should_wrap_response_to_result_if_object_given() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode = new ResourceNode("/test", HttpMethod.GET, TestDemo.class.getMethod("nonResponse"));
        ResourceItem item = new ResourceItem(resourceNode);
        ResponseResult result = item.resolve(new TestDemo(), RequestEntity.of(httpRequest));
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("test", result.getResult());
    }

    @Test
    void should_return_response_directly_if_response_given() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode = new ResourceNode("/test", HttpMethod.GET, TestDemo.class.getMethod("response"));
        ResourceItem item = new ResourceItem(resourceNode);
        ResponseResult result = item.resolve(new TestDemo(), RequestEntity.of(httpRequest));
        assertEquals(HttpResponseStatus.ACCEPTED, result.getStatus());
        assertEquals("test", result.getResult());
    }
}
