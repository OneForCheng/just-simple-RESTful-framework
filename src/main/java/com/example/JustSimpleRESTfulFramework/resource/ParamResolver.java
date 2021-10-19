package com.example.JustSimpleRESTfulFramework.resource;

import com.alibaba.fastjson.JSON;
import com.example.JustSimpleRESTfulFramework.annotation.QueryParam;
import com.example.JustSimpleRESTfulFramework.exception.BadRequestException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParamResolver {
    public static Object[] getArguments(Method method, Map<String, List<String>> params) {
        List<Object> parameterInstances = new LinkedList<>();
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                String paramName = parameter.getAnnotation(QueryParam.class).value();
                List<String> values = params.get(paramName);
                Object instance = JSON.parseObject(JSON.toJSONString(values.get(0)), parameter.getType());
                parameterInstances.add(instance);
            }
        }

        if (parameters.length != parameterInstances.size()) {
            throw new BadRequestException("Bad Request");
        }
        return parameterInstances.toArray();
    }
}
