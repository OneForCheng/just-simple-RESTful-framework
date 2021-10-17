package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.DELETE;
import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.POST;
import com.example.JustSimpleRESTfulFramework.annotation.PUT;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
}
