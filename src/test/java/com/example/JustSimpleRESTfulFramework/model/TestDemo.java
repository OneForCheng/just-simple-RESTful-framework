package com.example.JustSimpleRESTfulFramework.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import javax.ws.rs.GET;

public class TestDemo {

    public TestDemo create() {
        return new TestDemo();
    }

    @GET
    public String nonResponse() {
        return "test";
    }

    @GET
    public ResponseResult response() {
        return new ResponseResult(HttpResponseStatus.ACCEPTED, "test");
    }
}
