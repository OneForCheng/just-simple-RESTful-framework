package com.example.JustSimpleRESTfulFramework;

import com.example.JustSimpleRESTfulFramework.annotation.RouteResource;
import com.example.JustSimpleRESTfulFramework.model.TestResource;

@RouteResource(resources = { TestResource.class })
public class TestApp {
    public static void main(String[] args) {
        new RESTApplication().bootstrap(TestApp.class);
    }
}
