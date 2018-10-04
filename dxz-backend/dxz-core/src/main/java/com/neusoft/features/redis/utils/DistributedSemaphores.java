package com.neusoft.features.redis.utils;

import com.google.common.base.Charsets;

import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

public class DistributedSemaphores {
    public static String acquireFairSemaphore(JedisTemplate jedisTemplate, final String semaphoreName, final int limit, final long timeout) {
        return (String) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public String action(Jedis jedis) {
                String identifier = UUID.randomUUID().toString();

                String ownerZSet = semaphoreName + ":remote:owner";
                String counterKey = semaphoreName + ":remote:counterKey";
                long now = System.currentTimeMillis();
                Transaction t = jedis.multi();

                t.zremrangeByScore(semaphoreName.getBytes(Charsets.UTF_8), "-inf".getBytes(Charsets.UTF_8), String.valueOf(now - timeout).getBytes(Charsets.UTF_8));


                ZParams params = new ZParams();
                params.weights(new int[]{1, 0});

                t.zinterstore(ownerZSet, params, new String[]{ownerZSet, semaphoreName});
                Response<Long> c = t.incr(counterKey);
                t.exec();


                int counter = ((Long) c.get()).intValue();

                t = jedis.multi();
                t.zadd(semaphoreName, now, identifier);
                t.zadd(ownerZSet, counter, identifier);
                Response<Long> r = t.zrank(ownerZSet, identifier);
                t.exec();


                int rank = ((Long) r.get()).intValue();
                if (rank < limit) {
                    return identifier;
                }
                t = jedis.multi();
                t.zrem(semaphoreName, new String[]{identifier});
                t.zrem(ownerZSet, new String[]{identifier});
                t.exec();
                return null;
            }
        });
    }

    public static boolean releaseFairSemaphore(JedisTemplate jedisTemplate, final String semaphoreName, final String identifier) {
        return ((Boolean) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Boolean action(Jedis jedis) {
                Transaction t = jedis.multi();
                t.zrem(semaphoreName, new String[]{identifier});
                Response<Long> count = t.zrem(semaphoreName + ":remote:owner", new String[]{identifier});
                t.exec();
                return Boolean.valueOf(((Long) count.get()).intValue() == 1);
            }
        })).booleanValue();
    }
}