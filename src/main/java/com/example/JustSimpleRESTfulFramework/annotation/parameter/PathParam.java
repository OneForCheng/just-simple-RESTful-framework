package com.example.JustSimpleRESTfulFramework.annotation.parameter;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {
    String value();
}
