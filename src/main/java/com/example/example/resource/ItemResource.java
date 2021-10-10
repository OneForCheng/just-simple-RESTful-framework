package com.example.example.resource;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.JustSimpleRESTfulFramework.annotation.Path;
import com.example.example.model.Item;

@Path("/item")
public class ItemResource {
    @Path("content")
    public ItemContentResource getItemContentResource() {
        return new ItemContentResource();
    }

    @GET
    public Item get() {
        return new Item(1, "test");
    }
}
