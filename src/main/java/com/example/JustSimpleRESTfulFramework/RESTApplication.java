package com.example.JustSimpleRESTfulFramework;

import com.example.JustSimpleRESTfulFramework.config.BaseServerConfig;
import com.example.JustSimpleRESTfulFramework.server.HttpServer;

public final class RESTApplication {
    private BaseServerConfig serverConfig;

    public RESTApplication() {
        serverConfig = new BaseServerConfig();
    }

    public RESTApplication(BaseServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void bootstrap(Class<?> bootstrapClass) {
        new HttpServer(serverConfig).run();
    }
}
