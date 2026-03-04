package com.gk.study.permission;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {
    AccessLevel level() default AccessLevel.LOGIN;
}