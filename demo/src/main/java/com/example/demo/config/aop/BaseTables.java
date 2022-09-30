package com.example.demo.config.aop;

import java.lang.annotation.*;

/**
 * @author ljr
 * @date 2022-09-23 11:32
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaseTables {

    String value() default "";

}
