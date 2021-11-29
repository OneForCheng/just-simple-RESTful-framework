package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComponent;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComposite;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceItem;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.FullHttpRequest;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

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
        Arrays.stream(routeResources).forEach(resource -> resources.put(resource, getResourceComponent(UrlResolver.PATH_SEPARATOR, resource, null)));
        return resources;
    }

    private ResourceComponent getResourceComponent(String parentPath, Class<?> resource, Method resourceMethod) {
        ResourceComposite resourceComposite = new ResourceComposite(new ResourceNode(parentPath, null, resourceMethod));
        String resourceFullPath = AnnotationResolver.getResourceFullPath(parentPath, resource);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            String methodFullPath = AnnotationResolver.getResourceFullPath(resourceFullPath, method);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                ResourceItem resourceComponent = new ResourceItem(new ResourceNode(methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method), method));
                resourceComposite.add(resourceComponent);
            } else if (method.isAnnotationPresent(Path.class)) {
                ResourceComponent resourceComponent = getResourceComponent(methodFullPath, method.getReturnType(), method);
                resourceComposite.add(resourceComponent);
            }
        });
        return  resourceComposite;
    }

    public ResponseResult resolve(FullHttpRequest request) {
        try {
            RequestEntity requestEntity = RequestEntity.of(request);
            for (Map.Entry<Class<?>, ResourceComponent> resource : resources.entrySet()) {
                if (resource.getValue().isMatch(requestEntity)) {
                    return resource.getValue().resolve(injectContainer.getInstance(resource.getKey()), requestEntity);
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
