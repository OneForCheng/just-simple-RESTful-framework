package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.ParamResolver;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResourceComposite extends ResourceComponent {

    private final List<ResourceComponent> resourceComponents = new LinkedList<>();
    private final Class<?> resource;
    private final ResourceEntity resourceEntity;

    public ResourceComposite(Class<?> resource, ResourceEntity resourceEntity) {
        this.resource = resource;
        this.resourceEntity = resourceEntity;
    }

    @Override
    public Class<?> get() {
        return resource;
    }

    @Override
    public void add(ResourceComponent resourceComponent) {
        resourceComponents.add(resourceComponent);
    }

    @Override
    public void remove(ResourceComponent resourceComponent) {
        resourceComponents.remove(resourceComponent);
    }

    @Override
    public boolean isMatch(RequestEntity requestEntity) {
        for (ResourceComponent resourceComponent : resourceComponents) {
            if (resourceComponent.isMatch(requestEntity)) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    @Override
    public ResponseResult resolve(RequestEntity requestEntity, Object resourceInstance) {
        for (ResourceComponent resourceComponent : resourceComponents) {
            Object newResourceInstance = resourceInstance;
            if (resourceComponent instanceof ResourceComposite) {
                newResourceInstance = resourceComponent.getResourceInstance(resourceInstance, requestEntity);
            }
            ResponseResult responseResult = resourceComponent.resolve(requestEntity, newResourceInstance);
            if (responseResult != null) {
                return responseResult;
            }
        }
        return null;
    }

    @SneakyThrows
    @Override
    public Object getResourceInstance(Object resourceInstance, RequestEntity requestEntity) {
        Map<String, String> pathParameters = UrlResolver.getUrlPathParameters(resourceEntity.getUrl(), requestEntity.getPath());
        Object[] arguments = ParamResolver.getParameterInstances(resourceEntity.getMethod(), requestEntity, pathParameters);
        return resourceEntity.getMethod().invoke(resourceInstance, arguments);
    }
}
