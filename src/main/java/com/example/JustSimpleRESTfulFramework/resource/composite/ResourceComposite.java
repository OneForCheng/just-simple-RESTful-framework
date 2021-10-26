package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;

import java.util.LinkedList;
import java.util.List;

public class ResourceComposite extends ResourceComponent {

    private List<ResourceComponent> resources = new LinkedList<>();

    public void add(ResourceComponent resourceComponent) {
        resources.add(resourceComponent);
    }

    public void remove(ResourceComponent resourceComponent) {
        resources.remove(resourceComponent);
    }

    public boolean isMatch(RequestEntity requestEntity) {
        for (ResourceComponent resourceComponent : resources) {
            if (resourceComponent.isMatch(requestEntity)) {
                return true;
            }
        }
        return false;
    }
}
