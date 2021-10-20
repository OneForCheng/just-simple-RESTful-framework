package com.example.JustSimpleRESTfulFramework.resource;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UrlResolver {
    public static final String PATH_SEPARATOR = "/";
    private static final String PATH_PARAM_REGEX = "^\\{(\\w+)\\}$";


    public static String getBaseUrl(String uri) {
        String path = uri.split("\\?")[0];
        return combinePath(PATH_SEPARATOR, getFormattedPath(path));
    }

    public static Map<String, List<String>> getUrlQueryParameters(String uri) {
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

    private static boolean isPathParam(String path) {
        return Pattern.matches(PATH_PARAM_REGEX, path);
    }

    private static String getPathParamName(String path) {
        Pattern pattern = Pattern.compile(PATH_PARAM_REGEX);
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isMatchPath(String patternPath, String path) {
        String[] patternPaths = patternPath.split(PATH_SEPARATOR);
        String[] paths = path.split(PATH_SEPARATOR);
        if (patternPaths.length != paths.length) return false;
        for (int i = 0; i < patternPaths.length; i++) {
            if (!isPathParam(patternPaths[i]) && !patternPaths[i].equals(paths[i])) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, String> getUrlPathParameters(String patternPath, String path) {
        Map<String, String> pathParameters = new HashMap<>();
        String[] patternPaths = patternPath.split(PATH_SEPARATOR);
        String[] paths = path.split(PATH_SEPARATOR);
        for (int i = 0; i < patternPaths.length && i < paths.length; i++) {
            String pathParamName = getPathParamName(patternPaths[i]);
            if (pathParamName != null) {
                pathParameters.put(pathParamName, paths[i]);
            }
        }
        return pathParameters;
    }
}
