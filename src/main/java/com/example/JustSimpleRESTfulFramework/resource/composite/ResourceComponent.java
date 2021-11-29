package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.ParamResolver;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.SneakyThrows;

import java.util.Map;

public abstract class ResourceComponent {
    protected final ResourceNode resourceNode;

    public ResourceComponent(ResourceNode resourceNode) {
        this.resourceNode = resourceNode;
    }

    @SneakyThrows
    protected Object invoke(Object resourceInstance, RequestEntity requestEntity) {
        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(resourceNode.getUrl(), requestEntity.getPath());
        Object[] arguments = ParamResolver.getParameterInstances(resourceNode.getMethod(), requestEntity, pathParameters);
        return resourceNode.getMethod().invoke(resourceInstance, arguments);
    }

    public abstract void add(ResourceComponent resourceComponent);
    public abstract boolean isMatch(RequestEntity requestEntity);
    public abstract ResponseResult resolve(Object resourceInstance, RequestEntity requestEntity);

}

