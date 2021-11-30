package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.*;

class ResourceItemTest {

    public class Resource {
        @GET
        public String nonResponse() {
            return "test";
        }

        @GET
        public ResponseResult response() {
            return new ResponseResult(HttpResponseStatus.ACCEPTED, "test");
        }
    }

    @Test
    public void should_wrap_response_to_result_if_object_given() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceEntity = new ResourceNode("/test", HttpMethod.GET, Resource.class.getMethod("nonResponse"));
        ResourceItem item = new ResourceItem(resourceEntity);
        ResponseResult result = item.resolve(new Resource(), RequestEntity.of(httpRequest));
        assertEquals(HttpResponseStatus.OK, result.getStatus());
        assertEquals("test", result.getResult());
    }

    @Test
    public void should_return_response_directly_if_response_given() throws NoSuchMethodException {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceEntity = new ResourceNode("/test", HttpMethod.GET, Resource.class.getMethod("response"));
        ResourceItem item = new ResourceItem(resourceEntity);
        ResponseResult result = item.resolve(new Resource(), RequestEntity.of(httpRequest));
        assertEquals(HttpResponseStatus.ACCEPTED, result.getStatus());
        assertEquals("test", result.getResult());
    }
}
