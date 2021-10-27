package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;

public abstract class ResourceComponent {
    public abstract Class<?> get();
    public abstract void add(ResourceComponent resourceComponent);
    public abstract void remove(ResourceComponent resourceComponent);
    public abstract boolean isMatch(RequestEntity requestEntity);
    public abstract ResponseResult resolve(RequestEntity requestEntity, Object resourceInstance);
    protected abstract Object getResourceInstance(Object resourceInstance, RequestEntity requestEntity);
}

