package com.example.JustSimpleRESTfulFramework.config;

import io.netty.util.internal.StringUtil;

public class ServerConfig {
    public final static int DEFAULT_PORT = 8080;
    public final static int DEFAULT_MAX_CONTENT_LENGTH = 2048;

    public static boolean isSSL() {
        String ssl = System.getProperty("http.ssl");
        return !StringUtil.isNullOrEmpty(ssl);
    }

    public static String getProtocol() {
        return isSSL() ? "https" : "http";
    }

    public static int getPort() {
        String port = System.getProperty("http.proxyPort");
        if (StringUtil.isNullOrEmpty(port)) {
            return DEFAULT_PORT;
        }
        return Integer.parseInt(port);
    }

    public static int getMaxContentLength() {
        String maxContentLength = System.getProperty("http.maxContentLength");
        if (StringUtil.isNullOrEmpty(maxContentLength)) {
            return DEFAULT_MAX_CONTENT_LENGTH;
        }
        return Integer.parseInt(maxContentLength);
    }
}
