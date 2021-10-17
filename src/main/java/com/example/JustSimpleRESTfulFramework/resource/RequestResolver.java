package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.*;
import com.example.JustSimpleRESTfulFramework.model.RequestUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RequestResolver {
    private final Map<Class<?>, List<RequestUrlAndMethod>> resources;
    private final InjectContainer injectContainer;

    public RequestResolver(Class<?> bootstrapClass) {
        resources = getResources(bootstrapClass);
        injectContainer = getInjectContainer(bootstrapClass);
    }

    private Map<Class<?>, List<RequestUrlAndMethod>> getResources(Class<?> bootstrapClass) {
        Map<Class<?>, List<RequestUrlAndMethod>> resources = Collections.synchronizedMap(new HashMap<>());
        Class<?>[] routeResources = AnnotationResolver.getAnnotatedRouteResources(bootstrapClass);
        Arrays.stream(routeResources).forEach(resource -> {
            List<RequestUrlAndMethod> urls = getAllUrlAndMethodsOfResource(resource);
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

    private List<RequestUrlAndMethod> getAllUrlAndMethodsOfResource(Class<?> resource) {
        List<RequestUrlAndMethod> urls = new LinkedList<>();
        resolveUrlAndMethodOfResource(urls, UrlResolver.PATH_SEPARATOR, resource);
        return urls;
    }

    private void resolveUrlAndMethodOfResource(List<RequestUrlAndMethod> urls, String parentPath, Class<?> resource) {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlResolver.combinePath(parentPath, UrlResolver.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            boolean isRestAnnotationMethod = AnnotationResolver.isRestAnnotationMethod(method);
            if (isRestAnnotationMethod) {
                HttpMethod httpMethod = AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method);
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlResolver.combinePath(newParentPath, formattedPath);
                    urls.add(new RequestUrlAndMethod(url, httpMethod));
                } else {
                    urls.add(new RequestUrlAndMethod(newParentPath, httpMethod));
                }
            }
            if (!isRestAnnotationMethod && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlResolver.combinePath(newParentPath, UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value()));
                resolveUrlAndMethodOfResource(urls, nextParentPath, method.getReturnType());
            }
        });
    }

    private void resolveReturnResultOfResource(ResponseResult responseResult, RequestUrlAndMethod targetUrlAndMethod, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlResolver.combinePath(parentPath, UrlResolver.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        for (Method method : publicMethods) {
            boolean isRestAnnotationMethod = AnnotationResolver.isRestAnnotationMethod(method);
            if (isRestAnnotationMethod) {
                HttpMethod httpMethod = AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method);
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlResolver.combinePath(newParentPath, formattedPath);
                    if (url.equals(targetUrlAndMethod.getUrl()) && httpMethod.equals(targetUrlAndMethod.getMethod())) {
                        Object result = method.invoke(resourceInstance);
                        responseResult.setResult(result);
                        return;
                    }
                }
                if (newParentPath.equals(targetUrlAndMethod.getUrl()) && httpMethod.equals(targetUrlAndMethod.getMethod())) {
                    Object result = method.invoke(resourceInstance);
                    responseResult.setResult(result);
                    return;
                }
            }
            if (!isRestAnnotationMethod && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlResolver.combinePath(newParentPath, UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value()));
                Object returnTypeInstance = method.invoke(resourceInstance);
                resolveReturnResultOfResource(responseResult, targetUrlAndMethod, nextParentPath, method.getReturnType(), returnTypeInstance);
            }
        }
    }

    public ResponseResult resolve(FullHttpRequest request) {
        String uri = request.uri();
        HttpMethod method = request.method();
        try {
            String url  = UrlResolver.combinePath(UrlResolver.PATH_SEPARATOR, UrlResolver.getFormattedPath(UrlResolver.getBaseUrl(uri)));
            for (Map.Entry<Class<?>, List<RequestUrlAndMethod>> resource : resources.entrySet()) {
                if (resource.getValue().stream().anyMatch(item -> item.getUrl().equals(url) && item.getMethod().equals(method))) {
                    ResponseResult responseResult = new ResponseResult(OK, null);
                    Class<?> clazz = resource.getKey();
                    Object resourceInstance = injectContainer.getInstance(clazz);
                    resolveReturnResultOfResource(responseResult, new RequestUrlAndMethod(url, method), UrlResolver.PATH_SEPARATOR, clazz, resourceInstance);
                    return responseResult;
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
