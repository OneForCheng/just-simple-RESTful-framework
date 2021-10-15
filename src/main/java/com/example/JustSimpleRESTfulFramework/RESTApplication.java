package com.example.JustSimpleRESTfulFramework;

import com.example.JustSimpleRESTfulFramework.config.BaseServerConfig;
import com.example.JustSimpleRESTfulFramework.resource.RequestResolver;
import com.example.JustSimpleRESTfulFramework.server.HttpServer;

public final class RESTApplication {
    private HttpServer httpServer;

    public RESTApplication() {
        httpServer = new HttpServer(new BaseServerConfig());
    }

    public RESTApplication(BaseServerConfig serverConfig) {
        httpServer = new HttpServer(serverConfig);
    }

    public void bootstrap(Class<?> bootstrapClass) {
        httpServer.run(new RequestResolver(bootstrapClass));
    }
}
