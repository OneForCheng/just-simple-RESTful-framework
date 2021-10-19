package com.example.JustSimpleRESTfulFramework.resource;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlResolver {
    public static final String PATH_SEPARATOR = "/";

    public static String getBaseUrl(String uri) {
        String path = uri.split("\\?")[0];
        return combinePath(PATH_SEPARATOR, getFormattedPath(path));
    }

    public static Map<String, List<String>> getUrlParameters(String uri) {
        return new QueryStringDecoder(uri).parameters();
    }

    public static String getFormattedPath(String url) {
        return Arrays.stream(url.split(PATH_SEPARATOR)).filter(path -> !path.isEmpty()).collect(Collectors.joining(PATH_SEPARATOR)).toLowerCase(Locale.ROOT);
    }

    public static String combinePath(String parentPath, String childPath) {
        if (parentPath.equals(PATH_SEPARATOR)) {
            return childPath.isEmpty() ? parentPath : parentPath + childPath;
        }
        return childPath.isEmpty() ? parentPath : parentPath + PATH_SEPARATOR + childPath;
    }
}
