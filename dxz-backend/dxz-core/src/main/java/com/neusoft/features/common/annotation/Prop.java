package com.neusoft.features.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Property注解。
 * <p/>
 * 标识字段与properties文件中key的关系。
 *
 * @author andy.jiao@msn.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Prop {

    /**
     * property key
     */
    String key();

    /**
     * 找不到对应项时的默认值
     */
    String defaultValue() default "";
}
