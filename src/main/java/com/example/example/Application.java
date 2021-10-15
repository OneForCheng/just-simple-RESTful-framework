package com.example.example;

import com.example.JustSimpleRESTfulFramework.RESTApplication;
import com.example.JustSimpleRESTfulFramework.annotation.RouteResource;
import com.example.example.resource.ItemResource;

@RouteResource(resources = { ItemResource.class })
public class Application {
    public static void main(String[] args) {
        new RESTApplication().bootstrap(Application.class);
    }
}
