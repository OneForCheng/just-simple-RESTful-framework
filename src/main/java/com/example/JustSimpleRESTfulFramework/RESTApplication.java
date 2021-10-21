package com.example.JustSimpleRESTfulFramework;

import com.example.JustSimpleRESTfulFramework.config.BaseHttpServerConfig;
import com.example.JustSimpleRESTfulFramework.resource.RequestResolver;
import com.example.JustSimpleRESTfulFramework.server.HttpServer;

public final class RESTApplication {
    private HttpServer httpServer;

    public RESTApplication() {
        httpServer = new HttpServer(new BaseHttpServerConfig());
    }

    public RESTApplication(BaseHttpServerConfig httpServerConfig) {
        httpServer = new HttpServer(httpServerConfig);
    }

    public void bootstrap(Class<?> bootstrapClass) {
        httpServer.run(new RequestResolver(bootstrapClass));
    }

    public void close() {
        httpServer.close();
    }
}
