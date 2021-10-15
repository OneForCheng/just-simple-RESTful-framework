package com.example.JustSimpleRESTfulFramework.resource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassResolver {
    public static List<Method> getPublicMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(method -> Modifier.isPublic(method.getModifiers())).collect(Collectors.toList());
    }
}
