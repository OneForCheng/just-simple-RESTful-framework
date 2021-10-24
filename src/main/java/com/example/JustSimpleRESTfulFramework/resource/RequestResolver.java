package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.method.Path;
import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RequestResolver {
    private final Map<Class<?>, List<ResourceUrlAndMethod>> resources;
    private final InjectContainer injectContainer;

    public RequestResolver(Class<?> bootstrapClass) {
        resources = getResources(bootstrapClass);
        injectContainer = getInjectContainer(bootstrapClass);
    }

    private Map<Class<?>, List<ResourceUrlAndMethod>> getResources(Class<?> bootstrapClass) {
        Map<Class<?>, List<ResourceUrlAndMethod>> resources = Collections.synchronizedMap(new HashMap<>());
        Class<?>[] routeResources = AnnotationResolver.getAnnotatedRouteResources(bootstrapClass);
        Arrays.stream(routeResources).forEach(resource -> {
            List<ResourceUrlAndMethod> urls = new LinkedList<>();
            resolveUrlAndMethodOfResource(urls, UrlResolver.PATH_SEPARATOR, resource);
            resources.put(resource, urls);
        });
        return resources;
    }

    private InjectContainer getInjectContainer(Class<?> bootstrapClass) {
        InjectContainer injectContainer = new InjectContainer();
        Class<?>[] classQualifiers = AnnotationResolver.getAnnotatedClassQualifiers(bootstrapClass);
        Arrays.stream(classQualifiers).forEach(injectContainer::registerQualifiedClass);
        return injectContainer;
    }

    private void resolveUrlAndMethodOfResource(List<ResourceUrlAndMethod> urls, String parentPath, Class<?> resource) {
        String resourceFullPath = getFullResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            String methodFullPath = getFullResourcePath(method, resourceFullPath);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                urls.add(new ResourceUrlAndMethod(methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method)));
            } else if (method.isAnnotationPresent(Path.class)) {
                resolveUrlAndMethodOfResource(urls, methodFullPath, method.getReturnType());
            }
        });
    }

    private ResponseResult resolveResponseResultOfResource(RequestEntity requestEntity, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String resourceFullPath = getFullResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        for (Method method : publicMethods) {
            String methodFullPath = getFullResourcePath(method, resourceFullPath);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                if (isMatchUrlAndMethod(requestEntity, methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method))) {
                    Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(methodFullPath, requestEntity.getPath());
                    Object[] arguments = ParamResolver.getParameterInstances(method, requestEntity, pathParameters);
                    Object result1 = method.invoke(resourceInstance, arguments);
                    return new ResponseResult(OK, result1);
                }
            } else if (method.isAnnotationPresent(Path.class)) {
                Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(methodFullPath, requestEntity.getPath());
                Object[] arguments = ParamResolver.getParameterInstances(method, requestEntity, pathParameters);
                Object returnTypeInstance = method.invoke(resourceInstance, arguments);
                ResponseResult responseResult = resolveResponseResultOfResource(requestEntity, methodFullPath, method.getReturnType(), returnTypeInstance);
                if (responseResult != null) return responseResult;
            }
        }
        return null;
    }

    private boolean isMatchUrlAndMethod(RequestEntity requestEntity, String url, HttpMethod httpMethod) {
        return UrlResolver.isMatchPath(url, requestEntity.getPath()) && httpMethod.equals(requestEntity.getMethod());
    }

    private String getFullResourcePath(AnnotatedElement element, String parentPath) {
        if (element.isAnnotationPresent(Path.class)) {
            return UrlResolver.combinePath(parentPath, UrlResolver.getFormattedPath(element.getAnnotation(Path.class).value()));
        }
        return parentPath;
    }

    public ResponseResult resolve(FullHttpRequest request) {
        try {
            RequestEntity requestEntity = RequestEntity.of(request);
            for (Map.Entry<Class<?>, List<ResourceUrlAndMethod>> resource : resources.entrySet()) {
                if (resource.getValue().stream().anyMatch(item -> isMatchUrlAndMethod(requestEntity, item.getUrl(), item.getMethod()))) {
                    return resolveResponseResultOfResource(requestEntity, UrlResolver.PATH_SEPARATOR, resource.getKey(), injectContainer.getInstance(resource.getKey()));
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
