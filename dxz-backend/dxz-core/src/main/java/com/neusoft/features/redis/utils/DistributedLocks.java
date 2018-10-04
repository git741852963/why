package com.neusoft.features.redis.utils;

import com.google.common.base.Objects;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class DistributedLocks {
    public static String acquireLockWithTimeout(JedisTemplate jedisTemplate, String lockName, long acquireTimeout, final int lockTimeout) {
        final String identifier = UUID.randomUUID().toString();
        final String lockKey = "lock:" + lockName;

        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            boolean success = ((Boolean) jedisTemplate.execute(new JedisTemplate.JedisAction() {
                public Boolean action(Jedis jedis) {
                    if (jedis.setnx(lockKey, identifier).longValue() == 1L) {
                        jedis.pexpire(lockKey, lockTimeout);
                        return Boolean.TRUE;
                    }
                    if (jedis.ttl(lockKey).longValue() == -1L) {
                        jedis.pexpire(lockKey, lockTimeout);
                    }
                    return Boolean.FALSE;
                }
            })).booleanValue();
            if (success) {
                return identifier;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    public static boolean releaseLock(JedisTemplate jedisTemplate, String lockName, final String identifier) {
        final String lockKey = "lock:" + lockName;

        return ((Boolean) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Boolean action(Jedis jedis) {
                List<Object> results;
                do {
                    jedis.watch(new String[]{lockKey});
                    if (!Objects.equal(identifier, jedis.get(lockKey))) {
                        break;
                    }
                    Transaction t = jedis.multi();
                    t.del(lockKey);
                    results = t.exec();
                } while (results == null);

                jedis.unwatch();

                return Boolean.valueOf(false);
            }
        })).booleanValue();
    }
}