package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.Path;

public class TestSubResource {
    @GET
    @Path("get")
    public String get() {
        return "test_sub_resource";
    }
}
