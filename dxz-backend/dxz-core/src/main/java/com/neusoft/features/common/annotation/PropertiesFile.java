package com.neusoft.features.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Properties注解。
 * <p/>
 * 标识当前类与properties文件的对应关系。
 *
 * @author andy.jiao@msn.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PropertiesFile {

    /**
     * properties file name
     */
    String value();

    /**
     * properties directory
     */
    String dir() default "";
}
