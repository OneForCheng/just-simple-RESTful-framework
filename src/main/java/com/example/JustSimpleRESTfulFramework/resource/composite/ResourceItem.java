package com.example.JustSimpleRESTfulFramework.resource.composite;

import com.example.JustSimpleRESTfulFramework.model.RequestEntity;
import com.example.JustSimpleRESTfulFramework.model.ResourceEntity;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.ParamResolver;
import com.example.JustSimpleRESTfulFramework.resource.UrlResolver;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@AllArgsConstructor
public class ResourceItem extends ResourceComponent {

    private final ResourceEntity resourceEntity;

    @Override
    public Class<?> get() { return null; }

    @Override
    public void add(ResourceComponent resourceComponent) { }

    @Override
    public void remove(ResourceComponent resourceComponent) { }

    @Override
    public boolean isMatch(RequestEntity requestEntity) {
        return UrlResolver.isMatchPath(resourceEntity.getUrl(), requestEntity.getPath()) && resourceEntity.getHttpMethod().equals(requestEntity.getMethod());
    }

    @SneakyThrows
    @Override
    public ResponseResult resolve(RequestEntity requestEntity, Object resourceInstance) {
        if (isMatch(requestEntity)) {
            return new ResponseResult(OK, getResourceInstance(resourceInstance, requestEntity));
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
