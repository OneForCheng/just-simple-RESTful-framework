package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceUrlAndMethod;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResourceItem extends ResourceComponent {

    private final ResourceUrlAndMethod resourceUrlAndMethod;

    public void add(ResourceComponent resourceComponent) { }

    public void remove(ResourceComponent resourceComponent) { }

    public boolean isMatch(RequestEntity requestEntity) {
        return UrlResolver.isMatchPath(resourceUrlAndMethod.getUrl(), requestEntity.getPath()) && resourceUrlAndMethod.getMethod().equals(requestEntity.getMethod());
    }
}
