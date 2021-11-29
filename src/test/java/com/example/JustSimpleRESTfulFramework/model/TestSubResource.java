package com.example.JustSimpleRESTfulFramework.model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public class TestSubResource {
    @GET
    @Path("/get")
    public String get() {
        return "test_sub_resource";
    }
}
