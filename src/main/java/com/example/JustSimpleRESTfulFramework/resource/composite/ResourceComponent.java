package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.ParamResolver;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.SneakyThrows;

import java.util.Map;

public abstract class ResourceComponent {
    protected final ResourceEntity resourceEntity;

    public ResourceComponent(ResourceEntity resourceEntity) {
        this.resourceEntity = resourceEntity;
    }

    public abstract Class<?> get();
    public abstract void add(ResourceComponent resourceComponent);
    public abstract void remove(ResourceComponent resourceComponent);
    public abstract boolean isMatch(RequestEntity requestEntity);
    public abstract ResponseResult resolve(RequestEntity requestEntity, Object resourceInstance);

    @SneakyThrows
    public Object getResourceInstance(Object resourceInstance, RequestEntity requestEntity) {
        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(resourceEntity.getUrl(), requestEntity.getPath());
        Object[] arguments = ParamResolver.getParameterInstances(resourceEntity.getMethod(), requestEntity, pathParameters);
        return resourceEntity.getMethod().invoke(resourceInstance, arguments);
    }
}

