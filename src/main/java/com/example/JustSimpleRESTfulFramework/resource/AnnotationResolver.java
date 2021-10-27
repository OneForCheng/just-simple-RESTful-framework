package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.*;
import com.example.JustSimpleRESTfulFramework.annotation.method.*;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationResolver {
    private static final Map<Class, HttpMethod> REST_ANNOTATION_METHOD_MAP = new HashMap<>() {
        {
            put(GET.class, HttpMethod.GET);
            put(POST.class, HttpMethod.POST);
            put(PUT.class, HttpMethod.PUT);
            put(DELETE.class, HttpMethod.DELETE);
        }
    };

    public static boolean isRestAnnotationMethod(Method method) {
        return REST_ANNOTATION_METHOD_MAP.keySet().stream().anyMatch(method::isAnnotationPresent);
    }

    public static HttpMethod getHttpMethodFromRestAnnotationMethod(Method method) {
        Optional<Class> annotationMethod = REST_ANNOTATION_METHOD_MAP.keySet().stream().filter(method::isAnnotationPresent).findAny();
        return annotationMethod.map(REST_ANNOTATION_METHOD_MAP::get).orElse(null);
    }

    public static Class<?>[] getAnnotatedRouteResources(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RouteResource.class)) {
            return clazz.getAnnotation(RouteResource.class).resources();
        }
        return new Class[0];
    }

    public static Class<?>[] getAnnotatedClassQualifiers(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RouteResource.class)) {
            return clazz.getAnnotation(RouteResource.class).qualifiers();
        }
        return new Class[0];
    }

    public static String getResourceFullPath(String parentPath, AnnotatedElement element) {
        if (element.isAnnotationPresent(Path.class)) {
            return UrlResolver.combinePath(parentPath, UrlResolver.getFormattedPath(element.getAnnotation(Path.class).value()));
        }
        return parentPath;
    }
}
