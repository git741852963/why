package com.neusoft.features.db.annotation;

import com.neusoft.features.db.annotation.enums.GeneratedItem;
import com.neusoft.features.db.annotation.enums.GeneratedStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GeneratedValue.java
 *
 * @author andy.jiao@msn.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface GeneratedValue {

    GeneratedItem generator();

    GeneratedStrategy strategy() default GeneratedStrategy.DEFAULT;
}
