package com.neusoft.features.api.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * api版本号注解。
 *
 * @author andy.jiao@msn.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVer {
    /**
     * 最小版本号(初始版本号)
     */
    double min();

    /**
     * 最大版本号(支持最大版本号)
     */
    double max() default Double.MAX_VALUE;
}

