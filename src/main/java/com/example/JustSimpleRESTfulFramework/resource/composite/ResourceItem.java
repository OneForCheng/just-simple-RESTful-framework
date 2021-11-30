package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceNode;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.SneakyThrows;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class ResourceItem extends ResourceComponent {
    public ResourceItem(ResourceNode resourceNode) {
        super(resourceNode);
    }

    @Override
    public void add(ResourceComponent resourceComponent) { }

    @Override
    public boolean isMatch(RequestEntity requestEntity) {
        ResourceNode resourceNode = getResourceNode();
        return UrlResolver.isMatchPath(resourceNode.getUrl(), requestEntity.getPath()) && resourceNode.getHttpMethod().equals(requestEntity.getMethod());
    }

    @SneakyThrows
    @Override
    public ResponseResult resolve(Object resourceInstance, RequestEntity requestEntity) {
        if (isMatch(requestEntity)) {
            Object result = getResourceNode().invoke(resourceInstance, requestEntity);
            if (result instanceof ResponseResult) {
                return (ResponseResult)result;
            }
            return new ResponseResult(OK, result);
        }
        return null;
    }
}
