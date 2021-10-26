package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.method.Path;
import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComponent;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComposite;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceItem;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RequestResolver {
    private final Map<Class<?>, ResourceComponent> resources;
    private final InjectContainer injectContainer;

    public RequestResolver(Class<?> bootstrapClass) {
        injectContainer = getInjectContainer(bootstrapClass);
        resources = getResources(bootstrapClass);
    }

    private InjectContainer getInjectContainer(Class<?> bootstrapClass) {
        InjectContainer injectContainer = new InjectContainer();
        Class<?>[] classQualifiers = AnnotationResolver.getAnnotatedClassQualifiers(bootstrapClass);
        Arrays.stream(classQualifiers).forEach(injectContainer::registerQualifiedClass);
        return injectContainer;
    }

    private Map<Class<?>, ResourceComponent> getResources(Class<?> bootstrapClass) {
        Map<Class<?>, ResourceComponent> resources = Collections.synchronizedMap(new HashMap<>());
        Class<?>[] routeResources = AnnotationResolver.getAnnotatedRouteResources(bootstrapClass);
        Arrays.stream(routeResources).forEach(resource -> resources.put(resource, getResourceComponent(UrlResolver.PATH_SEPARATOR, resource)));
        return resources;
    }

    private ResourceComponent getResourceComponent(String parentPath, Class<?> resource) {
        ResourceComposite resourceComposite = new ResourceComposite();
        String resourceFullPath = getFullResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            String methodFullPath = getFullResourcePath(method, resourceFullPath);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                ResourceItem resourceComponent = new ResourceItem(new ResourceUrlAndMethod(methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method)));
                resourceComposite.add(resourceComponent);
            } else if (method.isAnnotationPresent(Path.class)) {
                ResourceComponent resourceComponent = getResourceComponent(methodFullPath, method.getReturnType());
                resourceComposite.add(resourceComponent);
            }
        });
        return  resourceComposite;
    }

    private ResponseResult resolveResponseResultOfResource(RequestEntity requestEntity, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String resourceFullPath = getFullResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        for (Method method : publicMethods) {
            String methodFullPath = getFullResourcePath(method, resourceFullPath);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                if (isMatchUrlAndHttpMethod(requestEntity, methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method))) {
                    return new ResponseResult(OK, getResourceMethodResult(resourceInstance, method, methodFullPath, requestEntity));
                }
            } else if (method.isAnnotationPresent(Path.class)) {
                ResponseResult responseResult = resolveResponseResultOfResource(requestEntity, methodFullPath, method.getReturnType(), getResourceMethodResult(resourceInstance, method, methodFullPath, requestEntity));
                if (responseResult != null) return responseResult;
            }
        }
        return null;
    }

    private Object getResourceMethodResult(Object resourceInstance, Method method, String methodFullPath, RequestEntity requestEntity) throws IllegalAccessException, InvocationTargetException {
        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(methodFullPath, requestEntity.getPath());
        Object[] arguments = ParamResolver.getParameterInstances(method, requestEntity, pathParameters);
        return method.invoke(resourceInstance, arguments);
    }

    private boolean isMatchUrlAndHttpMethod(RequestEntity requestEntity, String url, HttpMethod httpMethod) {
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
            for (Map.Entry<Class<?>, ResourceComponent> resource : resources.entrySet()) {
                if (resource.getValue().isMatch(requestEntity)) {
                    return resolveResponseResultOfResource(requestEntity, UrlResolver.PATH_SEPARATOR, resource.getKey(), injectContainer.getInstance(resource.getKey()));
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
