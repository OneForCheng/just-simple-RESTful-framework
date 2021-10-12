package com.example.JustSimpleRESTfulFramework.resource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class UrlUtil {
    public static final String PATH_SEPARATOR = "/";

    public static String getFormattedPath(String url) {
        return Arrays.stream(url.split(PATH_SEPARATOR)).filter(path -> !path.isEmpty()).collect(Collectors.joining(PATH_SEPARATOR)).toLowerCase(Locale.ROOT);
    }

    public static String combinePath(String parentPath, String path) {
        if (parentPath.equals(PATH_SEPARATOR)) {
            return path.isEmpty() ? parentPath : parentPath + path;
        }
        return path.isEmpty() ? parentPath : parentPath + PATH_SEPARATOR + path;
    }
}
