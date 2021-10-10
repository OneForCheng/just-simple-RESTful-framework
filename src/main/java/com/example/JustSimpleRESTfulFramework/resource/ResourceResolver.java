package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.Path;
import com.example.JustSimpleRESTfulFramework.annotation.RESTResource;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceResolver {
    private final Map<Class<?>, List<String>> resources;
    private final String PATH_SEPARATOR = "/";

    public ResourceResolver(Class<?> bootstrapClass) {
        resources = getRESTResources(bootstrapClass);
    }

    private Map<Class<?>, List<String>> getRESTResources(Class<?> bootstrapClass) {
        Map<Class<?>, List<String>> resources = Collections.synchronizedMap(new HashMap<>());
        if (bootstrapClass.isAnnotationPresent(RESTResource.class)) {
            RESTResource annotation = bootstrapClass.getAnnotation(RESTResource.class);
            Arrays.stream(annotation.value()).forEach(resource -> {
                List<String> urls = getAllUrlsOfResource(resource);
                resources.put(resource, urls);
            });
        }
        return resources;
    }

    private List<String> getAllUrlsOfResource(Class<?> resource) {
        List<String> urls = new LinkedList<>();
        resolveUrlOfResource(urls, PATH_SEPARATOR, resource);
        return urls;
    }

    public void resolveUrlOfResource(List<String> urls, String parentPath, Class<?> resource) {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = combinePath(parentPath, getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = Arrays.stream(resource.getMethods()).filter(method -> Modifier.isPublic(method.getModifiers())).collect(Collectors.toList());
        publicMethods.forEach(method -> {
            if (method.isAnnotationPresent(GET.class)) {
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  combinePath(newParentPath, formattedPath);
                    urls.add(url);
                } else {
                    urls.add(newParentPath);
                }
            }
            if (!method.isAnnotationPresent(GET.class) && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = combinePath(newParentPath, getFormattedPath(method.getAnnotation(Path.class).value()));
                resolveUrlOfResource(urls, nextParentPath, method.getReturnType());
            }
        });
    }

    public ResponseResult resolveUri(String uri) {
        try {
            String url  = combinePath(PATH_SEPARATOR, getFormattedPath(uri.split("\\?")[0]));
            for (Map.Entry<Class<?>, List<String>> resource : resources.entrySet()) {
                if (resource.getValue().contains(url)) {
                    return new ResponseResult(OK, url);
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }

    private String getFormattedPath(String url) {
        return Arrays.stream(url.split(PATH_SEPARATOR)).filter(path -> !path.isEmpty()).collect(Collectors.joining(PATH_SEPARATOR)).toLowerCase(Locale.ROOT);
    }

    private String combinePath(String parentPath, String path) {
        if (parentPath.equals(PATH_SEPARATOR)) {
            return path.isEmpty() ? parentPath : parentPath + path;
        }
        return path.isEmpty() ? parentPath : parentPath + PATH_SEPARATOR + path;
    }
}
