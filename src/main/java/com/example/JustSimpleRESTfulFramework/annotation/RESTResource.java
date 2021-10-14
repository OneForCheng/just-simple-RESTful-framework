package com.example.JustSimpleRESTfulFramework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RESTResource {
    Class<?>[] resources() default {};
    Class<?>[] qualifiers() default {};
}
