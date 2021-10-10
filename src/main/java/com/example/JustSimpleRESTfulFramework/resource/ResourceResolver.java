package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.RESTResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ResourceResolver {
    Set<Class<?>> resources;

    public ResourceResolver(Class<?> bootstrapClass) {
        resources = getRESTResources(bootstrapClass);
    }

    Set<Class<?>> getRESTResources(Class<?> bootstrapClass) {
        Set<Class<?>> classes = Collections.synchronizedSet(new LinkedHashSet<>());
        if (bootstrapClass.isAnnotationPresent(RESTResource.class)) {
            RESTResource annotation = bootstrapClass.getAnnotation(RESTResource.class);
            classes.addAll(Arrays.asList(annotation.value()));
        }
        return classes;
    }
}
