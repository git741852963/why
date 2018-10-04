package com.neusoft.features.common.annotation;

import java.lang.annotation.*;

/**
 * 错误码注解。
 *
 * @author andy.jiao@msn.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseCode {
    String value();

    String reason() default "发生异常";
}
