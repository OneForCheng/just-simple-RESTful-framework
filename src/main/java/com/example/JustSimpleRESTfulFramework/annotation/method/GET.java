package com.example.JustSimpleRESTfulFramework.annotation.method;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GET {
}