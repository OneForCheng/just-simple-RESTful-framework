package com.example.example.resource;

import com.example.JustSimpleRESTfulFramework.annotation.GET;
import com.example.example.model.ItemContent;

public class ItemContentResource {
    @GET
    public ItemContent get() {
        return new ItemContent(2.5, 10);
    }
}
