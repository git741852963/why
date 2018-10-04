package com.neusoft.features.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fields {
    private static final Logger log = LoggerFactory.getLogger(Fields.class);

    public static FieldGetter createGetter(Class clazz, String fieldName) {
        if (Map.class.isAssignableFrom(clazz)) {
            return new MapFieldGetter(fieldName);
        }
        return new ObjectFieldGetter(clazz, fieldName);
    }

    public static abstract interface FieldGetter {
        public abstract Object get(Object paramObject);
    }

    public static class MapFieldGetter
            implements FieldGetter {
        private Object key;

        public MapFieldGetter(Object key) {
            this.key = key;
        }

        public Object get(Object o) {
            return ((Map) o).get(this.key);
        }
    }

    public static class ObjectFieldGetter
            implements FieldGetter {
        private Method method;

        public ObjectFieldGetter(Class clazz, String fieldName) {
            String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try {
                this.method = clazz.getMethod(methodName, new Class[0]);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("no such method found for field " + fieldName + " in class " + clazz.getName());
            }
        }

        public Object get(Object o) {
            try {
                return this.method.invoke(o, new Object[0]);
            } catch (IllegalAccessException e) {
                Fields.log.warn("error when get field", e);
            } catch (InvocationTargetException e) {
                Fields.log.warn("error when get field", e);
            }
            return null;
        }
    }
}
