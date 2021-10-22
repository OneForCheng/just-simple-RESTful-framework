package com.example.JustSimpleRESTfulFramework.resource;

import com.alibaba.fastjson.JSON;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.PathParam;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.QueryParam;
import com.example.JustSimpleRESTfulFramework.annotation.parameter.RequestBody;
import com.example.JustSimpleRESTfulFramework.exception.BadRequestException;
import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParamResolver {
    public static Object[] getParameterInstances(Method method, RequestEntity requestEntity, Map<String, String> pathParameters) {
        List<Object> parameterInstances = new LinkedList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                String paramName = parameter.getAnnotation(QueryParam.class).value();
                List<String> paramValues = requestEntity.getQueryParameters().get(paramName);
                Object parameterInstance = getQueryParameterInstance(parameter.getType(), paramValues);
                parameterInstances.add(parameterInstance);
            } else if (parameter.isAnnotationPresent(PathParam.class)) {
                String paramName = parameter.getAnnotation(PathParam.class).value();
                parameterInstances.add(pathParameters.get(paramName));
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                String body = requestEntity.getBody().toString(CharsetUtil.UTF_8);
                parameterInstances.add(JSON.parseObject(body, parameter.getType()));
            }
        }

        if (parameters.length != parameterInstances.size()) {
            throw new BadRequestException("Bad Request");
        }
        return parameterInstances.toArray();
    }

    private static Object getQueryParameterInstance(Class<?> paramType, List<String> paramValues) {
        if (paramValues == null) return null;
        if (paramType.isArray() || paramType.equals(List.class)) return JSON.parseObject(JSON.toJSONString(paramValues), paramType);
        if (paramType.equals(String.class)) return paramValues.get(0);
        return JSON.parseObject(paramValues.get(0), paramType);
    }
}
