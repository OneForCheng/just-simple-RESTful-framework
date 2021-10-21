package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.POST;
import com.example.JustSimpleRESTfulFramework.annotation.Path;
import com.example.JustSimpleRESTfulFramework.annotation.QueryParam;

@Path("/test")
public class TestResource {
    @GET
    public String get() {
        return "test_get";
    }

    @POST
    public String post() {
        return "test_post";
    }

    @Path("/sub-resource")
    public TestSubResource SubResource() {
        return new TestSubResource();
    }

    @GET
    @Path("/string-query-param")
    public String get(@QueryParam("name") String name) {
        return name;
    }

    @GET
    @Path("/integer-query-param")
    public Integer get(@QueryParam("count") Integer count) {
        return count;
    }

    @GET
    @Path("/array-query-param")
    public String[] get(@QueryParam("tags") String[] tags) {
        return tags;
    }
}
