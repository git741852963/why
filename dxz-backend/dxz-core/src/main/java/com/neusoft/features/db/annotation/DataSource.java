package com.neusoft.features.db.annotation;

import com.neusoft.features.db.annotation.enums.DataSourceElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DataSource.java
 *
 * @author andy.jiao@msn.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSource {
    DataSourceElement value();
}
