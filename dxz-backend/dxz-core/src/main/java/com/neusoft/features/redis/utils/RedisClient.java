package com.neusoft.features.redis.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public abstract class RedisClient {
    public static Long listLen(JedisTemplate jedisTemplate, final String key) {
        return (Long) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Long action(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    public static Long listRemOne(JedisTemplate jedisTemplate, String key, Object val) {
        return listRem(jedisTemplate, key, val, Long.valueOf(1L));
    }

    public static Long listRemAll(JedisTemplate jedisTemplate, String key, Object val) {
        return listRem(jedisTemplate, key, val, Long.valueOf(0L));
    }

    private static Long listRem(JedisTemplate jedisTemplate, final String key, final Object val, final Long count) {
        return (Long) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Long action(Jedis jedis) {
                return jedis.lrem(key, count.longValue(), String.valueOf(val));
            }
        });
    }

    public static Long listRemOne(JedisTemplate jedisTemplate, List<String> keys, Object val) {
        return listRem(jedisTemplate, keys, val, Long.valueOf(1L));
    }

    public static Long listRemAll(JedisTemplate jedisTemplate, List<String> keys, Object val) {
        return listRem(jedisTemplate, keys, val, Long.valueOf(0L));
    }

    private static Long listRem(JedisTemplate jedisTemplate, final List<String> keys, final Object val, final Long count) {
        return (Long) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Long action(Jedis jedis) {
                Pipeline p = jedis.pipelined();
                Long deleted = Long.valueOf(0L);
                for (String key : keys) {
                    deleted = Long.valueOf(deleted.longValue() + ((Long) p.lrem(key, count.longValue(), String.valueOf(val)).get()).longValue());
                }
                p.sync();
                return deleted;
            }
        });
    }

    public static List<Long> listPaging2Long(JedisTemplate jedisTemplate, final String key, final Integer offset, final Integer limit) {
        return (List<Long>) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public List<Long> action(Jedis jedis) {
                List<String> ids = jedis.lrange(key, offset.intValue(), offset.intValue() + limit.intValue() - 1);
                List<Long> newList = Lists.transform(ids, new Function<String, Long>() {
                    public Long apply(String s) {
                        return s == null ? null : Long.valueOf(s);
                    }
                });
                return newList;
            }
        });
    }

    public static Long listAdd(JedisTemplate jedisTemplate, final List<String> keys, final String val) {
        return (Long) jedisTemplate.execute(new JedisTemplate.JedisAction() {
            public Long action(Jedis jedis) {
                Pipeline p = jedis.pipelined();
                Long pushed = Long.valueOf(0L);
                for (String key : keys) {
                    p.lpush(key, new String[]{val});
                    pushed = Long.valueOf(pushed.longValue() + 1L);
                }
                p.sync();
                return pushed;
            }
        });
    }
}
