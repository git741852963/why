package com.neusoft.features.db.mybatis.plugin;

import com.neusoft.features.db.annotation.GeneratedValue;
import com.neusoft.features.db.annotation.enums.GeneratedItem;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * GeneratedValuePlugin
 *
 * @author andy.jiao@msn.com
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class GeneratedValuePlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }

        Class<?> parameterClass = parameter.getClass();
        Class<?> parameterSupperClass = parameterClass.getSuperclass();
        List<Field> fieldList = new ArrayList<>();
        if (parameterSupperClass != null) {
            Field[] fields = parameterSupperClass.getDeclaredFields();
            for (int index = 0; index < fields.length; index++) {
                fieldList.add(fields[index]);
            }
        }
        Field[] fields = parameterClass.getDeclaredFields();
        for (int index = 0; index < fields.length; index++) {
            fieldList.add(fields[index]);
        }

        Date currentDate = new Date();

        if (SqlCommandType.INSERT == sqlCommandType && fieldList != null && fieldList.size() > 0) {
            // isnert
            for (Field field : fieldList) {
                if (field.isAnnotationPresent(GeneratedValue.class)) {
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    field.setAccessible(true);

                    switch (generatedValue.generator()) {
                        case CREATEAT:
                        case UPDATEAT:
                            field.set(parameter, currentDate);
                            break;
                        case ISDELETE:
                            field.set(parameter, false);
                            break;
                        default:
                            break;
                    }

                    field.setAccessible(false);
                }
            }
        } else if (SqlCommandType.UPDATE == sqlCommandType && fieldList != null && fieldList.size() > 0) {
            // update
            for (Field field : fieldList) {
                if (field.isAnnotationPresent(GeneratedValue.class)) {
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    field.setAccessible(true);
                    if (generatedValue.generator().equals(GeneratedItem.UPDATEAT)) {
                        field.set(parameter, currentDate);
                    }
                    field.setAccessible(false);
                }
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
