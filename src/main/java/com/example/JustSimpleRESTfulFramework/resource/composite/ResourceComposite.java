package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

public class ResourceComposite extends ResourceComponent {

    private final List<ResourceComponent> resourceComponents = new LinkedList<>();

    public ResourceComposite(ResourceNode resourceNode) {
        super(resourceNode);
    }

    @Override
    public void add(ResourceComponent resourceComponent) {
        resourceComponents.add(resourceComponent);
    }

    @Override
    public boolean isMatch(RequestEntity requestEntity) {
        return resourceComponents.stream().anyMatch(resourceComponent -> resourceComponent.isMatch(requestEntity));
    }

    @SneakyThrows
    @Override
    public ResponseResult resolve(Object resourceInstance, RequestEntity requestEntity) {
        for (ResourceComponent resourceComponent : resourceComponents) {
            Object newResourceInstance = resourceInstance;
            if (resourceComponent instanceof ResourceComposite) {
                newResourceInstance = resourceComponent.getResourceNode().invoke(resourceInstance, requestEntity);
            }
            ResponseResult responseResult = resourceComponent.resolve(newResourceInstance, requestEntity);
            if (responseResult != null) {
                return responseResult;
            }
        }
        return null;
    }
}
