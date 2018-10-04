package com.neusoft.features.redis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

public class JedisTemplate {
    private static Logger log = LoggerFactory.getLogger(JedisTemplate.class);
    private Pool<Jedis> jedisPool;

    @Autowired
    public JedisTemplate(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            log.error("Redis connection lost.", e);
            throw e;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisConnectionException e) {
            log.error("Redis connection lost.", e);
            throw e;
        } finally {
            jedis.close();
        }
    }

    //    protected void closeResource(Jedis jedis, boolean connectionBroken) {
    //        if (jedis != null) {
    //            if (connectionBroken) {
    //                this.jedisPool.returnBrokenResource(jedis);
    //            } else {
    //                this.jedisPool.returnResource(jedis);
    //            }
    //        }
    //    }

    public Pool<Jedis> getJedisPool() {
        return this.jedisPool;
    }

    public static abstract interface JedisActionNoResult {
        public abstract void action(Jedis paramJedis);
    }

    public static abstract interface JedisAction<T> {
        public abstract T action(Jedis paramJedis);
    }
}
