package com.neusoft.features.redis.dao;

import java.util.List;

/**
 * Redis Interface
 *
 * @author andy.jiao@msn.com
 */
public interface RedisDao<T> {

    public List<T> hgetAll(String key, Class<T> clazz);

    public T hget(String key, String field, Class<T> clazz);

    public void hset(String key, String field, Object object);
}
