package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;

public abstract class ResourceComponent {
    public abstract void add(ResourceComponent resourceComponent);
    public abstract void remove(ResourceComponent resourceComponent);
    public abstract boolean isMatch(RequestEntity requestEntity);
}

