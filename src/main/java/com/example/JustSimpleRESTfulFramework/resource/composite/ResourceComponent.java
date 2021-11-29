package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;

public abstract class ResourceComponent {
    private final ResourceNode resourceNode;

    public ResourceComponent(ResourceNode resourceNode) {
        this.resourceNode = resourceNode;
    }
    public ResourceNode getResourceNode() { return resourceNode; }

    public abstract void add(ResourceComponent resourceComponent);
    public abstract boolean isMatch(RequestEntity requestEntity);
    public abstract ResponseResult resolve(Object resourceInstance, RequestEntity requestEntity);
}

