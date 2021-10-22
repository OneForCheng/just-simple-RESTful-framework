package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.method.Path;
import com.example.JustSimpleRESTfulFramework.model.RequestParam;
import com.example.JustSimpleRESTfulFramework.model.ResourceUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

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
            List<ResourceUrlAndMethod> urls = getAllUrlAndMethodsOfResource(resource);
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

    private List<ResourceUrlAndMethod> getAllUrlAndMethodsOfResource(Class<?> resource) {
        List<ResourceUrlAndMethod> urls = new LinkedList<>();
        resolveUrlAndMethodOfResource(urls, UrlResolver.PATH_SEPARATOR, resource);
        return urls;
    }

    private void resolveUrlAndMethodOfResource(List<ResourceUrlAndMethod> urls, String parentPath, Class<?> resource) {
        String newParentPath = getCurrentResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            boolean isRestAnnotationMethod = AnnotationResolver.isRestAnnotationMethod(method);
            if (isRestAnnotationMethod) {
                HttpMethod httpMethod = AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method);
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlResolver.combinePath(newParentPath, formattedPath);
                    urls.add(new ResourceUrlAndMethod(url, httpMethod));
                } else {
                    urls.add(new ResourceUrlAndMethod(newParentPath, httpMethod));
                }
            }
            if (!isRestAnnotationMethod && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlResolver.combinePath(newParentPath, UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value()));
                resolveUrlAndMethodOfResource(urls, nextParentPath, method.getReturnType());
            }
        });
    }

    private boolean resolveReturnResultOfResource(ResponseResult responseResult, RequestParam requestParam, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String newParentPath = getCurrentResourcePath(resource, parentPath);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        for (Method method : publicMethods) {
            boolean isRestAnnotationMethod = AnnotationResolver.isRestAnnotationMethod(method);
            if (isRestAnnotationMethod) {
                HttpMethod httpMethod = AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method);
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlResolver.combinePath(newParentPath, formattedPath);
                    if (UrlResolver.isMatchPath(url, requestParam.getPath()) && httpMethod.equals(requestParam.getMethod())) {
                        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(url, requestParam.getPath());
                        Object[] arguments = ParamResolver.getParameterInstances(method, requestParam, pathParameters);
                        Object result = method.invoke(resourceInstance, arguments);
                        responseResult.setResult(result);
                        return true;
                    }
                } else if (UrlResolver.isMatchPath(newParentPath, requestParam.getPath()) && httpMethod.equals(requestParam.getMethod())) {
                    Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(newParentPath, requestParam.getPath());
                    Object[] arguments = ParamResolver.getParameterInstances(method, requestParam, pathParameters);
                    Object result = method.invoke(resourceInstance, arguments);
                    responseResult.setResult(result);
                    return true;
                }
            }
            if (!isRestAnnotationMethod && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlResolver.combinePath(newParentPath, UrlResolver.getFormattedPath(method.getAnnotation(Path.class).value()));
                Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(nextParentPath, requestParam.getPath());
                Object[] arguments = ParamResolver.getParameterInstances(method, requestParam, pathParameters);
                Object returnTypeInstance = method.invoke(resourceInstance, arguments);
                boolean hasSetResult = resolveReturnResultOfResource(responseResult, requestParam, nextParentPath, method.getReturnType(), returnTypeInstance);
                if (hasSetResult) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getCurrentResourcePath(Class<?> resource, String parentPath) {
        if (resource.isAnnotationPresent(Path.class)) {
            return UrlResolver.combinePath(parentPath, UrlResolver.getFormattedPath(resource.getAnnotation(Path.class).value()));
        }
        return parentPath;
    }

    public ResponseResult resolve(FullHttpRequest request) {
        try {
            RequestParam requestParam = getRequestParam(request);
            for (Map.Entry<Class<?>, List<ResourceUrlAndMethod>> resource : resources.entrySet()) {
                if (resource.getValue().stream().anyMatch(item -> UrlResolver.isMatchPath(item.getUrl(), requestParam.getPath()) && item.getMethod().equals(requestParam.getMethod()))) {
                    ResponseResult responseResult = new ResponseResult(OK, null);
                    Class<?> clazz = resource.getKey();
                    Object resourceInstance = injectContainer.getInstance(clazz);
                    resolveReturnResultOfResource(responseResult, requestParam, UrlResolver.PATH_SEPARATOR, clazz, resourceInstance);
                    return responseResult;
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }

    private RequestParam getRequestParam(FullHttpRequest request) {
        String uri = request.uri();
        String path  = UrlResolver.getBaseUrl(uri);
        Map<String, List<String>> parameters = UrlResolver.getUrlQueryParameters(uri);
        HttpMethod method = request.method();
        ByteBuf content = request.content();
        return new RequestParam(path, parameters, method, content);
    }
}
