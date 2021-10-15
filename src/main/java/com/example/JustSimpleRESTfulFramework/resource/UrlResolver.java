package com.example.JustSimpleRESTfulFramework.resource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class UrlResolver {
    public static final String PATH_SEPARATOR = "/";

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
