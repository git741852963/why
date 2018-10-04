package com.neusoft.features.session.redis;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

public class JedisPoolExecutor {
    private volatile Pool<Jedis> jedisPool;

    public JedisPoolExecutor(JedisPoolConfig config, boolean sentinel, Properties params) {
        if (sentinel) {
            String sentinelProps = params.getProperty("session.redis.sentinel.hosts");
            Iterable<String> parts = Splitter.on(',').trimResults().omitEmptyStrings().split(sentinelProps);

            Set<String> sentinelHosts = Sets.newHashSet(parts);
            String masterName = params.getProperty("session.redis.sentinel.masterName");
            this.jedisPool = new JedisSentinelPool(masterName, sentinelHosts, config);
        } else {
            String redisHost = params.getProperty("session.redis.host");
            int redisPort = Integer.parseInt(params.getProperty("session.redis.port"));
            this.jedisPool = new JedisPool(config, redisHost, redisPort);
        }
    }

    public <V> V execute(JedisCallback<V> cb) {
        Jedis jedis = (Jedis) this.jedisPool.getResource();
        boolean success = true;
        try {
            return cb.execute(jedis);
        } catch (JedisException e) {
            success = false;
            if (jedis != null) {
                this.jedisPool.returnBrokenResource(jedis);
            }
            throw e;
        } finally {
            if (success) {
                this.jedisPool.returnResource(jedis);
            }
        }
    }

    public Pool<Jedis> getJedisPool() {
        return this.jedisPool;
    }
}
