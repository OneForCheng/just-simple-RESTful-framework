package com.example.example.resource;

import com.example.JustSimpleRESTfulFramework.annotation.*;
import com.example.example.model.Item;

import java.util.List;

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

    @GET
    @Path("/{id}")
    public String getPathId(@PathParam("id") String pathId) {
        return pathId;
    }

    @GET
    @Path("list")
    public List<String> list(@QueryParam("name") List<String> name) {
        return name;
    }

    @POST
    public Item post() {
        return new Item(2, "test 2");
    }
}
