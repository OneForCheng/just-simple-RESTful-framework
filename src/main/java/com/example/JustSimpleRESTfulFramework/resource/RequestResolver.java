package com.example.JustSimpleRESTfulFramework.resource;

import com.example.JustSimpleRESTfulFramework.annotation.method.Path;
import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComponent;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceComposite;
import com.example.JustSimpleRESTfulFramework.resource.composite.ResourceItem;
import com.thoughtworks.InjectContainer.InjectContainer;
import io.netty.handler.codec.http.FullHttpRequest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

public class RequestResolver {
    private final List<ResourceComponent> resources;
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

    private List<ResourceComponent> getResources(Class<?> bootstrapClass) {
        List<ResourceComponent> resources = new LinkedList<>();
        Class<?>[] routeResources = AnnotationResolver.getAnnotatedRouteResources(bootstrapClass);
        Arrays.stream(routeResources).forEach(resource -> resources.add(getResourceComponent(UrlResolver.PATH_SEPARATOR, resource, null)));
        return resources;
    }

    private ResourceComponent getResourceComponent(String parentPath, Class<?> resource, Method resourceMethod) {
        ResourceComposite resourceComposite = new ResourceComposite(resource, new ResourceEntity(parentPath, null, resourceMethod));
        String resourceFullPath = AnnotationResolver.getResourceFullPath(parentPath, resource);
        List<Method> publicMethods = ClassResolver.getPublicMethods(resource);
        publicMethods.forEach(method -> {
            String methodFullPath = AnnotationResolver.getResourceFullPath(resourceFullPath, method);
            if (AnnotationResolver.isRestAnnotationMethod(method)) {
                ResourceItem resourceComponent = new ResourceItem(new ResourceEntity(methodFullPath, AnnotationResolver.getHttpMethodFromRestAnnotationMethod(method), method));
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
            for (ResourceComponent resource: resources) {
                if (resource.isMatch(requestEntity)) {
                    return resource.resolve(requestEntity, injectContainer.getInstance(resource.get()));
                }
            }
        } catch (Exception e) {
            return new ResponseResult(INTERNAL_SERVER_ERROR, e);
        }
        return new ResponseResult(NOT_FOUND, NOT_FOUND.reasonPhrase());
    }
}
