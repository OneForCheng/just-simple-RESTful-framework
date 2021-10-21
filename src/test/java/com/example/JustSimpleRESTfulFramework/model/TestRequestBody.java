package com.example.JustSimpleRESTfulFramework.model;

public class TestRequestBody {
    private final String id;
    private final String name;
    private final Double age;

    public TestRequestBody(String id, String name, Double age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getAge() {
        return age;
    }
}
