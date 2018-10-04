package com.neusoft.features.redis.utils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 负责生成redis key，已废弃。
 */
@Deprecated
public abstract class KeyUtils {
    public static <T> String entityCount(Class<T> entityClass) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, entityClass.getSimpleName()) + ":count";
    }

    public static <T> String entityId(Class<T> entityClass, long id) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, entityClass.getSimpleName()) + ":" + id;
    }

    public static <T> String entityId(String prefix, long id) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, prefix) + ":" + id;
    }

    public static <T> String entityId(Class<T> entityClass, String id) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id can not be null");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, entityClass.getSimpleName()) + ":" + id;
    }
}
