package com.neusoft.features.session.redis;

import redis.clients.jedis.Jedis;

public abstract interface JedisCallback<V> {
    public abstract V execute(Jedis paramJedis);
}