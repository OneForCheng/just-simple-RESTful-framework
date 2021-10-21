package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.Path;

@Path("/test")
public class TestResource {
    @GET
    public String get() {
        return "test";
    }
}
