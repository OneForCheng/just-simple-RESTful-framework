package com.example.JustSimpleRESTfulFramework;

import com.example.JustSimpleRESTfulFramework.server.HttpServer;

public final class RESTApplication {
    public void bootstrap(Class<?> bootstrapClass) {
        new HttpServer().run();
    }
}
