package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.*;
import com.example.JustSimpleRESTfulFramework.model.RequestUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.HttpMethod;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceResolver {
    private final Map<Class<?>, List<RequestUrlAndMethod>> resources;
    private final InjectContainer injectContainer;
    private final Map<Class, HttpMethod> REST_ANNOTATION_METHOD_MAP = new HashMap<>() {
        {
            put(GET.class, HttpMethod.GET);
            put(POST.class, HttpMethod.POST);
            put(PUT.class, HttpMethod.PUT);
            put(DELETE.class, HttpMethod.DELETE);
        }
    };

    public ResourceResolver(Class<?> bootstrapClass) {
        resources = getResources(bootstrapClass);
        injectContainer = getInjectContainer(bootstrapClass);
    }

    private Map<Class<?>, List<RequestUrlAndMethod>> getResources(Class<?> bootstrapClass) {
        Map<Class<?>, List<RequestUrlAndMethod>> resources = Collections.synchronizedMap(new HashMap<>());
        if (bootstrapClass.isAnnotationPresent(RouteResource.class)) {
            RouteResource annotation = bootstrapClass.getAnnotation(RouteResource.class);
            Arrays.stream(annotation.resources()).forEach(resource -> {
                List<RequestUrlAndMethod> urls = getAllUrlAndMethodsOfResource(resource);
                resources.put(resource, urls);
            });
        }
        return resources;
    }

    private InjectContainer getInjectContainer(Class<?> bootstrapClass) {
        InjectContainer injectContainer = new InjectContainer();
        if (bootstrapClass.isAnnotationPresent(RouteResource.class)) {
            RouteResource annotation = bootstrapClass.getAnnotation(RouteResource.class);
            Arrays.stream(annotation.qualifiers()).forEach(injectContainer::registerQualifiedClass);
        }
        return injectContainer;
    }

    private List<RequestUrlAndMethod> getAllUrlAndMethodsOfResource(Class<?> resource) {
        List<RequestUrlAndMethod> urls = new LinkedList<>();
        resolveUrlAndMethodOfResource(urls, UrlUtil.PATH_SEPARATOR, resource);
        return urls;
    }

    private void resolveUrlAndMethodOfResource(List<RequestUrlAndMethod> urls, String parentPath, Class<?> resource) {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlUtil.combinePath(parentPath, UrlUtil.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = getPublicMethods(resource);
        publicMethods.forEach(method -> {
            Optional<Class> annotationMethod = REST_ANNOTATION_METHOD_MAP.keySet().stream().filter(method::isAnnotationPresent).findAny();
            if (annotationMethod.isPresent()) {
                HttpMethod httpMethod = REST_ANNOTATION_METHOD_MAP.get(annotationMethod.get());
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlUtil.combinePath(newParentPath, formattedPath);
                    urls.add(new RequestUrlAndMethod(url, httpMethod));
                } else {
                    urls.add(new RequestUrlAndMethod(newParentPath, httpMethod));
                }
            }
            if (annotationMethod.isEmpty() && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlUtil.combinePath(newParentPath, UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value()));
                resolveUrlAndMethodOfResource(urls, nextParentPath, method.getReturnType());
            }
        });
    }

    private void resolveReturnResultOfResource(ResponseResult responseResult, RequestUrlAndMethod targetUrlAndMethod, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlUtil.combinePath(parentPath, UrlUtil.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = getPublicMethods(resource);
        for (Method method : publicMethods) {
            Optional<Class> annotationMethod = REST_ANNOTATION_METHOD_MAP.keySet().stream().filter(method::isAnnotationPresent).findAny();
            if (annotationMethod.isPresent()) {
                HttpMethod httpMethod = REST_ANNOTATION_METHOD_MAP.get(annotationMethod.get());
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlUtil.combinePath(newParentPath, formattedPath);
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
            if (annotationMethod.isEmpty() && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlUtil.combinePath(newParentPath, UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value()));
                Object returnTypeInstance = method.invoke(resourceInstance);
                resolveReturnResultOfResource(responseResult, targetUrlAndMethod, nextParentPath, method.getReturnType(), returnTypeInstance);
            }
        }
    }

    private List<Method> getPublicMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(method -> Modifier.isPublic(method.getModifiers())).collect(Collectors.toList());
    }

    public ResponseResult resolveUriAndMethod(String uri, HttpMethod method) {
        try {
            String url  = UrlUtil.combinePath(UrlUtil.PATH_SEPARATOR, UrlUtil.getFormattedPath(uri.split("\\?")[0]));
            for (Map.Entry<Class<?>, List<RequestUrlAndMethod>> resource : resources.entrySet()) {
                if (resource.getValue().stream().anyMatch(item -> item.getUrl().equals(url) && item.getMethod().equals(method))) {
                    ResponseResult responseResult = new ResponseResult(OK, null);
                    Class<?> clazz = resource.getKey();
                    Object resourceInstance = injectContainer.getInstance(clazz);
                    resolveReturnResultOfResource(responseResult, new RequestUrlAndMethod(url, method), UrlUtil.PATH_SEPARATOR, clazz, resourceInstance);
                    return responseResult;
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
