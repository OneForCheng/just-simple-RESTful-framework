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
        return UrlResolver.isMatchPath(resourceNode.getUrl(), requestEntity.getPath()) && resourceNode.getHttpMethod().equals(requestEntity.getMethod());
    }

    @SneakyThrows
    @Override
    public ResponseResult resolve(Object resourceInstance, RequestEntity requestEntity) {
        if (isMatch(requestEntity)) {
            return new ResponseResult(OK, resourceNode.invoke(resourceInstance, requestEntity));
        }
        return null;
    }
}
