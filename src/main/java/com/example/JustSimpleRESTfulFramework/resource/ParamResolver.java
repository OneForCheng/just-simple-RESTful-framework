package com.example.JustSimpleRESTfulFramework.resource;

import com.alibaba.fastjson.JSON;
import com.example.JustSimpleRESTfulFramework.annotation.PathParam;
import com.example.JustSimpleRESTfulFramework.annotation.QueryParam;
import com.example.JustSimpleRESTfulFramework.exception.BadRequestException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParamResolver {
    public static Object[] getArguments(Method method, Map<String, List<String>> queryParameters, Map<String, String> pathParameters) {
        List<Object> parameterInstances = new LinkedList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                String paramName = parameter.getAnnotation(QueryParam.class).value();
                List<String> paramValues = queryParameters.get(paramName);
                Object parameterInstance = getParameterInstance(parameter.getType(), paramValues);
                parameterInstances.add(parameterInstance);
            } else if (parameter.isAnnotationPresent(PathParam.class)) {
                String paramName = parameter.getAnnotation(PathParam.class).value();
                parameterInstances.add(pathParameters.get(paramName));
            }
        }

        if (parameters.length != parameterInstances.size()) {
            throw new BadRequestException("Bad Request");
        }
        return parameterInstances.toArray();
    }

    public static Object getParameterInstance(Class<?> paramType, List<String> paramValues) {
        if (paramValues == null) return null;
        if (paramType.isArray() || paramType.equals(List.class)) return JSON.parseObject(JSON.toJSONString(paramValues), paramType);
        if (paramType.equals(String.class)) return paramValues.get(0);
        return JSON.parseObject(paramValues.get(0), paramType);
    }
}
