package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.*;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.thoughtworks.InjectContainer.InjectContainer;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceResolver {
    private final Map<Class<?>, List<String>> resources;
    private final InjectContainer injectContainer;

    public ResourceResolver(Class<?> bootstrapClass) {
        resources = getRESTResources(bootstrapClass);
        injectContainer = new InjectContainer();
    }

    private Map<Class<?>, List<String>> getRESTResources(Class<?> bootstrapClass) {
        Map<Class<?>, List<String>> resources = Collections.synchronizedMap(new HashMap<>());
        if (bootstrapClass.isAnnotationPresent(RESTResource.class)) {
            RESTResource annotation = bootstrapClass.getAnnotation(RESTResource.class);
            Arrays.stream(annotation.value()).forEach(resource -> {
                List<String> urls = getAllUrlsOfResource(resource);
                resources.put(resource, urls);
            });
        }
        return resources;
    }

    private List<String> getAllUrlsOfResource(Class<?> resource) {
        List<String> urls = new LinkedList<>();
        resolveUrlOfResource(urls, UrlUtil.PATH_SEPARATOR, resource);
        return urls;
    }

    public void resolveUrlOfResource(List<String> urls, String parentPath, Class<?> resource) {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlUtil.combinePath(parentPath, UrlUtil.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = Arrays.stream(resource.getMethods()).filter(method -> Modifier.isPublic(method.getModifiers())).collect(Collectors.toList());
        publicMethods.forEach(method -> {
            if (method.isAnnotationPresent(GET.class)) {
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlUtil.combinePath(newParentPath, formattedPath);
                    urls.add(url);
                } else {
                    urls.add(newParentPath);
                }
            }
            if (!method.isAnnotationPresent(GET.class) && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlUtil.combinePath(newParentPath, UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value()));
                resolveUrlOfResource(urls, nextParentPath, method.getReturnType());
            }
        });
    }

    public void populateResponseResult(ResponseResult responseResult, String url, Class<?> resource) throws InvocationTargetException, IllegalAccessException {
        Object resourceInstance = injectContainer.getInstance(resource);
        resolveReturnResultOfResource(responseResult, url, UrlUtil.PATH_SEPARATOR, resource, resourceInstance);
    }

    public void resolveReturnResultOfResource(ResponseResult responseResult, String targetUrl, String parentPath, Class<?> resource, Object resourceInstance) throws InvocationTargetException, IllegalAccessException {
        String newParentPath;
        if (resource.isAnnotationPresent(Path.class)) {
            newParentPath = UrlUtil.combinePath(parentPath, UrlUtil.getFormattedPath(resource.getAnnotation(Path.class).value()));
        } else {
            newParentPath = parentPath;
        }
        List<Method> publicMethods = Arrays.stream(resource.getMethods()).filter(method -> Modifier.isPublic(method.getModifiers())).collect(Collectors.toList());
        for (Method method : publicMethods) {
            if (method.isAnnotationPresent(GET.class)) {
                if (method.isAnnotationPresent(Path.class)) {
                    String formattedPath = UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value());
                    String url =  UrlUtil.combinePath(newParentPath, formattedPath);
                    if (url.equals(targetUrl)) {
                        Object result = method.invoke(resourceInstance);
                        responseResult.setResult(result);
                        return;
                    }
                }
                if (newParentPath.equals(targetUrl)) {
                    Object result = method.invoke(resourceInstance);
                    responseResult.setResult(result);
                    return;
                }
            }
            if (!method.isAnnotationPresent(GET.class) && method.isAnnotationPresent(Path.class)) {
                String nextParentPath = UrlUtil.combinePath(newParentPath, UrlUtil.getFormattedPath(method.getAnnotation(Path.class).value()));
                Object returnTypeInstance = method.invoke(resourceInstance);
                resolveReturnResultOfResource(responseResult, targetUrl, nextParentPath, method.getReturnType(), returnTypeInstance);
            }
        }
    }

    public ResponseResult resolveUri(String uri) {
        try {
            String url  = UrlUtil.combinePath(UrlUtil.PATH_SEPARATOR, UrlUtil.getFormattedPath(uri.split("\\?")[0]));
            for (Map.Entry<Class<?>, List<String>> resource : resources.entrySet()) {
                if (resource.getValue().contains(url)) {
                    ResponseResult responseResult = new ResponseResult(OK, null);
                    populateResponseResult(responseResult, url, resource.getKey());
                    return responseResult;
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
