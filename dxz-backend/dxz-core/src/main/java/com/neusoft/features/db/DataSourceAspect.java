package com.neusoft.features.db;

import com.neusoft.features.db.annotation.DataSource;
import com.neusoft.features.db.annotation.enums.DataSourceElement;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * DataSourceAspect.java
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public class DataSourceAspect {

    //TODO:此类应该废弃，使用mycat切换数据源
    public void before(JoinPoint point) {
        Object target = point.getTarget();
        String method = point.getSignature().getName();
        Class<?> selfClassz = target.getClass();
        Class<?>[] selfParameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        Method m;
        try {
            m = selfClassz.getMethod(method, selfParameterTypes);
            if (m != null) {
                if (m.isAnnotationPresent(DataSource.class)) {
                    // 添加DataSource
                    DataSource data = m.getAnnotation(DataSource.class);
                    DynamicDataSourceHolder.putDataSource(data.value().value());
                    log.debug("Acquired Connection [ " + data.value().value() + " ] datasource for JDBC");
                } else {
                    // 未添加DataSource数据库选择slave
                    DynamicDataSourceHolder.putDataSource(DataSourceElement.SLAVE_00.value());
                    log.debug("Acquired Connection [ " + DataSourceElement.SLAVE_00.value() + " ] datasource for JDBC");
                }
            } else {
                // 未找到选择slave
                DynamicDataSourceHolder.putDataSource(DataSourceElement.SLAVE_00.value());
                log.debug("Acquired Connection [ " + DataSourceElement.SLAVE_00.value() + " ] datasource for JDBC");
            }
        } catch (Exception ex) {
            // #IGNORE
        }

    }
}
