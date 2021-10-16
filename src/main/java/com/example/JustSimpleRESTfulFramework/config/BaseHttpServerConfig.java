package com.example.JustSimpleRESTfulFramework.config;

import io.netty.util.internal.StringUtil;

public class BaseHttpServerConfig {
    public final int DEFAULT_PORT = 8080;
    public final int DEFAULT_MAX_CONTENT_LENGTH = 2048;

    public boolean isSSL() {
        String ssl = System.getProperty("http.ssl");
        return !StringUtil.isNullOrEmpty(ssl);
    }

    public String getProtocol() {
        return isSSL() ? "https" : "http";
    }

    public int getPort() {
        String port = System.getProperty("http.proxyPort");
        if (StringUtil.isNullOrEmpty(port)) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(port);
    }

    public int getMaxContentLength() {
        String maxContentLength = System.getProperty("http.maxContentLength");
        if (StringUtil.isNullOrEmpty(maxContentLength)) {
            return DEFAULT_MAX_CONTENT_LENGTH;
        }
        return Integer.parseInt(maxContentLength);
    }
}
