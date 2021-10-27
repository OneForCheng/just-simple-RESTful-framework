package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

public class ResourceComposite extends ResourceComponent {

    private final List<ResourceComponent> resourceComponents = new LinkedList<>();
    private final Class<?> resource;

    public ResourceComposite(Class<?> resource, ResourceEntity resourceEntity) {
        super(resourceEntity);
        this.resource = resource;
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
}
