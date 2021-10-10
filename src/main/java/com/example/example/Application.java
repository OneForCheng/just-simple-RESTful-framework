package com.example.example;

import com.example.JustSimpleRESTfulFramework.RESTApplication;
import com.example.JustSimpleRESTfulFramework.annotation.RESTResource;
import com.example.example.resource.ItemResource;

@RESTResource({ ItemResource.class })
public class Application {
    public static void main(String[] args) {
        new RESTApplication().bootstrap(Application.class);
    }
}
