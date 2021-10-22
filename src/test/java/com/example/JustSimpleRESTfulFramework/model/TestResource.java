package com.example.JustSimpleRESTfulFramework.model;

import com.example.JustSimpleRESTfulFramework.annotation.method.GET;
import com.example.JustSimpleRESTfulFramework.annotation.method.POST;
import com.example.JustSimpleRESTfulFramework.annotation.method.Path;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.PathParam;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.QueryParam;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.RequestBody;

import java.util.List;

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

    @GET
    @Path("/list-query-param")
    public List<String> get(@QueryParam("tags") List<String> tags) {
        return tags;
    }

    @GET
    @Path("/path-param/{id}")
    public String getPathId(@PathParam("id") String id) {
        return id;
    }

    @POST
    @Path("/request-body")
    public TestRequestBody getRequestBody(@RequestBody TestRequestBody body) {
        return body;
    }
}
