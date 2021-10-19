package com.example.example.resource;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.POST;
import com.example.JustSimpleRESTfulFramework.annotation.Path;
import com.example.JustSimpleRESTfulFramework.annotation.QueryParam;
import com.example.example.model.Item;

@Path("/item")
public class ItemResource {
    @Path("content")
    public ItemContentResource getItemContentResource() {
        return new ItemContentResource();
    }

    @GET
    public Item get(@QueryParam("name") String name) {
        return new Item(1, name);
    }

    @POST
    public Item post() {
        return new Item(2, "test 2");
    }
}
