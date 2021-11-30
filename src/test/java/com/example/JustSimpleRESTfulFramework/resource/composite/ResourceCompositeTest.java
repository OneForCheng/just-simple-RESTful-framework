package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceCompositeTest {
    @Test
    void should_return_false_if_resource_component_list_are_empty() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode = new ResourceNode("/test", null, null);
        ResourceComposite resourceComposite = new ResourceComposite(resourceNode);
        assertFalse(resourceComposite.isMatch(RequestEntity.of(httpRequest)));
    }

    @Test
    void should_return_false_if_all_of_resource_component_list_are_not_matched() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode1 = new ResourceNode("/test", null, null);
        ResourceNode resourceNode2 = new ResourceNode("/no-test", HttpMethod.GET, null);
        ResourceNode resourceNode3 = new ResourceNode("/test", HttpMethod.POST, null);
        ResourceComposite resourceComposite = new ResourceComposite(resourceNode1);
        resourceComposite.add(new ResourceItem(resourceNode2));
        resourceComposite.add(new ResourceItem(resourceNode3));
        ResourceComposite otherResourceComponent = new ResourceComposite(null);
        otherResourceComponent.add(new ResourceItem(resourceNode2));
        otherResourceComponent.add(new ResourceItem(resourceNode3));
        resourceComposite.add(otherResourceComponent);
        assertFalse(resourceComposite.isMatch(RequestEntity.of(httpRequest)));
    }

    @Test
    void should_return_true_if_any_one_of_resource_component_list_is_matched() {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test");
        ResourceNode resourceNode1 = new ResourceNode("/test", null, null);
        ResourceNode resourceNode2 = new ResourceNode("/no-test", HttpMethod.GET, null);
        ResourceNode resourceNode3 = new ResourceNode("/test", HttpMethod.GET, null);
        ResourceComposite resourceComposite = new ResourceComposite(resourceNode1);
        resourceComposite.add(new ResourceItem(resourceNode2));
        resourceComposite.add(new ResourceItem(resourceNode2));
        ResourceComposite otherResourceComponent = new ResourceComposite(null);
        otherResourceComponent.add(new ResourceItem(resourceNode2));
        otherResourceComponent.add(new ResourceItem(resourceNode3));
        resourceComposite.add(otherResourceComponent);
        assertTrue(resourceComposite.isMatch(RequestEntity.of(httpRequest)));
    }
}
