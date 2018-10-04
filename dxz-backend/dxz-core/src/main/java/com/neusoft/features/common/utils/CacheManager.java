package com.neusoft.features.common.utils;

import com.google.common.cache.LoadingCache;

import java.util.List;

/**
 * 内存缓存管理。
 *
 * 关于内存缓存请参考：com.google.common.cache.LoadingCache
 *
 * @author andy.jiao@msn.com
 */
public abstract class CacheManager {

    public static void refresh(LoadingCache<Long, ?> cache, List<Long> keys) {
        if ((keys == null) || (keys.size() <= 0)) {
            return;
        }
        refresh(cache, (Long[]) keys.toArray(new Long[0]));
    }

    public static void refresh(LoadingCache<Long, ?> cache, Long... keys) {
        if ((cache != null) && (keys != null) && (keys.length > 0)) {
            for (Long key : keys) {
                cache.refresh(key);
            }
        }
    }

    public static void invalidate(LoadingCache<Long, ?> cache, List<Long> keys) {
        if ((keys == null) || (keys.size() <= 0)) {
            return;
        }
        invalidate(cache, (Long[]) keys.toArray(new Long[0]));
    }

    public static void invalidate(LoadingCache<Long, ?> cache, Long... keys) {
        if ((cache != null) && (keys != null) && (keys.length > 0)) {
            for (Long key : keys) {
                cache.invalidate(key);
            }
        }
    }
}

