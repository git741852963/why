package com.neusoft.features.redis;

import com.neusoft.features.redis.utils.JedisTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis standalone / cluster mode adapter for jedis 2.9.0。
 * <p/>
 * 由于Jedis和JedisCluster继承的接口互相之间没有继承关系，所以只能笨点做了。
 * <p/>
 * 除了JedisCommands以外的4个接口是两两对应的：
 * MultyKeyCommands：MultiKeyJedisClusterCommands
 * ScriptingCommands：JedisClusterScriptingCommands
 * <p/>
 * 以下Jedis实现的4个接口未实现，需要的时候再说：
 * AdvancedJedisCommands, BasicCommands, ClusterCommands, SentinelCommands。
 *
 * @author andy.jiao@msn.com
 */
@Component
public class JedisAdapter implements JedisCommands, MultiKeyJedisClusterCommands, JedisClusterScriptingCommands, MultiKeyCommands, ScriptingCommands, InitializingBean {

    // region [Members]
    @Autowired(required = false)
    private JedisCluster jedisCluster;

    @Autowired(required = false)
    private JedisTemplate jedisTemplate;

    private boolean isCluster;
    // endregion

    // region [Constructor]
//    /**
//     * 构造函数
//     */
//    @Autowired(required = false)
//    public JedisAdapter(JedisCluster jedisCluster, JedisTemplate jedisTemplate) {
//
//        if (jedisCluster == null && jedisTemplate == null) {
//            throw new IllegalArgumentException("expected Jedis or JedisCluster");
//        }
//
//        if (jedisCluster != null) {
//            this.jedisCluster = jedisCluster;
//            isCluster = true;
//        } else {
//            this.jedisTemplate = jedisTemplate;
//            isCluster = false;
//        }
//    }
    //endregion

    // region [Private Methods]
    public boolean isCluster() {
        return isCluster;
    }

    public void setCluster(boolean isCluster) {
        this.isCluster = isCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }

    private JedisTemplate getJedisTemplate() {
        if (isCluster) {
            throw new IllegalStateException("this command only support in standalone mode.");
        }
        return jedisTemplate;
    }

    private JedisCluster getJedisCluster() {
        if (!isCluster) {
            throw new IllegalStateException("this command only support in cluster mode.");
        }
        return jedisCluster;
    }
    // endregion

    // region [JedisCommands] methods
    @Override
    public String set(final String key, final String value) {
        return isCluster ? jedisCluster.set(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.set(key, value);
                    }
                });
    }

    @Override
    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        return isCluster ? jedisCluster.set(key, value, nxxx, expx, time) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.set(key, value, nxxx, expx, time);
                    }
                });
    }

    @Override
    public String set(final String key, final String value, final String nxxx) {
        return isCluster ? jedisCluster.set(key, value, nxxx) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.set(key, value, nxxx);
                    }
                });
    }

    @Override
    public String get(final String key) {
        return isCluster ? jedisCluster.get(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public Boolean exists(final String key) {
        return isCluster ? jedisCluster.exists(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Boolean>() {
            @Override
            public Boolean action(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long persist(final String key) {
        return isCluster ? jedisCluster.persist(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.persist(key);
            }
        });
    }

    @Override
    public String type(final String key) {
        return isCluster ? jedisCluster.type(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.type(key);
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return isCluster ? jedisCluster.expire(key, seconds) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.expire(key, seconds);
                    }
                });
    }

    @Override
    public Long pexpire(final String key, final long milliseconds) {
        return isCluster ? jedisCluster.pexpire(key, milliseconds) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.pexpire(key, milliseconds);
                    }
                });
    }

    @Override
    public Long expireAt(final String key, final long unixTime) {
        return isCluster ? jedisCluster.expireAt(key, unixTime) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.expireAt(key, unixTime);
                    }
                });
    }

    @Override
    public Long pexpireAt(final String key, final long millisecondsTimestamp) {
        return isCluster ? jedisCluster.pexpireAt(key, millisecondsTimestamp) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.pexpireAt(key, millisecondsTimestamp);
                    }
                });
    }

    @Override
    public Long ttl(final String key) {
        return isCluster ? jedisCluster.ttl(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    @Override
    public Long pttl(final String key) {
        return isCluster ? jedisCluster.pttl(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.pttl(key);
            }
        });
    }

    @Override
    public Boolean setbit(final String key, final long offset, final boolean value) {
        return isCluster ? jedisCluster.setbit(key, offset, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Boolean>() {
                    @Override
                    public Boolean action(Jedis jedis) {
                        return jedis.setbit(key, offset, value);
                    }
                });
    }

    @Override
    public Boolean setbit(final String key, final long offset, final String value) {
        return isCluster ? jedisCluster.setbit(key, offset, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Boolean>() {
                    @Override
                    public Boolean action(Jedis jedis) {
                        return jedis.setbit(key, offset, value);
                    }
                });
    }

    @Override
    public Boolean getbit(final String key, final long offset) {
        return isCluster ? jedisCluster.getbit(key, offset) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Boolean>() {
                    @Override
                    public Boolean action(Jedis jedis) {
                        return jedis.getbit(key, offset);
                    }
                });
    }

    @Override
    public Long setrange(final String key, final long offset, final String value) {
        return isCluster ? jedisCluster.setrange(key, offset, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.setrange(key, offset, value);
                    }
                });
    }

    @Override
    public String getrange(final String key, final long startOffset, final long endOffset) {
        return isCluster ? jedisCluster.getrange(key, startOffset, endOffset) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.getrange(key, startOffset, endOffset);
                    }
                });
    }

    @Override
    public String getSet(final String key, final String value) {
        return isCluster ? jedisCluster.getSet(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.getSet(key, value);
                    }
                });
    }

    @Override
    public Long setnx(final String key, final String value) {
        return isCluster ? jedisCluster.setnx(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.setnx(key, value);
                    }
                });
    }

    @Override
    public String setex(final String key, final int seconds, final String value) {
        return isCluster ? jedisCluster.setex(key, seconds, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.setex(key, seconds, value);
                    }
                });
    }

    @Override
    public String psetex(final String key, final long milliseconds, final String value) {
        return isCluster ? jedisCluster.psetex(key, milliseconds, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.psetex(key, milliseconds, value);
                    }
                });
    }

    @Override
    public Long decrBy(final String key, final long integer) {
        return isCluster ? jedisCluster.decrBy(key, integer) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.decrBy(key, integer);
                    }
                });
    }

    @Override
    public Long decr(final String key) {
        return isCluster ? jedisCluster.decr(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    @Override
    public Long incrBy(final String key, final long integer) {
        return isCluster ? jedisCluster.incrBy(key, integer) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.incrBy(key, integer);
                    }
                });
    }

    @Override
    public Double incrByFloat(final String key, final double value) {
        return isCluster ? jedisCluster.incrByFloat(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.incrByFloat(key, value);
                    }
                });
    }

    @Override
    public Long incr(final String key) {
        return isCluster ? jedisCluster.incr(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    @Override
    public Long append(final String key, final String value) {
        return isCluster ? jedisCluster.append(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.append(key, value);
                    }
                });
    }

    @Override
    public String substr(final String key, final int start, final int end) {
        return isCluster ? jedisCluster.substr(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.substr(key, start, end);
                    }
                });
    }

    @Override
    public Long hset(final String key, final String field, final String value) {
        return isCluster ? jedisCluster.hset(key, field, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.hset(key, field, value);
                    }
                });
    }

    @Override
    public String hget(final String key, final String field) {
        return isCluster ? jedisCluster.hget(key, field) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.hget(key, field);
                    }
                });
    }

    @Override
    public Long hsetnx(final String key, final String field, final String value) {
        return isCluster ? jedisCluster.hsetnx(key, field, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.hsetnx(key, field, value);
                    }
                });
    }

    @Override
    public String hmset(final String key, final Map<String, String> hash) {
        return isCluster ? jedisCluster.hmset(key, hash) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.hmset(key, hash);
                    }
                });
    }

    @Override
    public List<String> hmget(final String key, final String... fields) {
        return isCluster ? jedisCluster.hmget(key, fields) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.hmget(key, fields);
                    }
                });
    }

    @Override
    public Long hincrBy(final String key, final String field, final long value) {
        return isCluster ? jedisCluster.hincrBy(key, field, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.hincrBy(key, field, value);
                    }
                });
    }

    @Override
    public Double hincrByFloat(final String key, final String field, final double value) {
        return isCluster ? jedisCluster.hincrByFloat(key, field, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.hincrByFloat(key, field, value);
                    }
                });
    }

    @Override
    public Boolean hexists(final String key, final String field) {
        return isCluster ? jedisCluster.hexists(key, field) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Boolean>() {
                    @Override
                    public Boolean action(Jedis jedis) {
                        return jedis.hexists(key, field);
                    }
                });
    }

    @Override
    public Long hdel(final String key, final String... field) {
        return isCluster ? jedisCluster.hdel(key, field) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    @Override
    public Long hlen(final String key) {
        return isCluster ? jedisCluster.hlen(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    @Override
    public Set<String> hkeys(final String key) {
        return isCluster ? jedisCluster.hkeys(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.hkeys(key);
                    }
                });
    }

    @Override
    public List<String> hvals(final String key) {
        return isCluster ? jedisCluster.hvals(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.hvals(key);
                    }
                });
    }

    @Override
    public Map<String, String> hgetAll(final String key) {
        return isCluster ? jedisCluster.hgetAll(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Map<String, String>>() {
                    @Override
                    public Map<String, String> action(Jedis jedis) {
                        return jedis.hgetAll(key);
                    }
                });
    }

    @Override
    public Long rpush(final String key, final String... string) {
        return isCluster ? jedisCluster.rpush(key, string) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.rpush(key, string);
                    }
                });
    }

    @Override
    public Long lpush(final String key, final String... string) {
        return isCluster ? jedisCluster.lpush(key, string) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.lpush(key, string);
                    }
                });
    }

    @Override
    public Long llen(final String key) {
        return isCluster ? jedisCluster.llen(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    @Override
    public List<String> lrange(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.lrange(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.lrange(key, start, end);
                    }
                });
    }

    @Override
    public String ltrim(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.ltrim(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.ltrim(key, start, end);
                    }
                });
    }

    @Override
    public String lindex(final String key, final long index) {
        return isCluster ? jedisCluster.lindex(key, index) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.lindex(key, index);
                    }
                });
    }

    @Override
    public String lset(final String key, final long index, final String value) {
        return isCluster ? jedisCluster.lset(key, index, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.lset(key, index, value);
                    }
                });
    }

    @Override
    public Long lrem(final String key, final long count, final String value) {
        return isCluster ? jedisCluster.lrem(key, count, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.lrem(key, count, value);
                    }
                });
    }

    @Override
    public String lpop(final String key) {
        return isCluster ? jedisCluster.lpop(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    @Override
    public String rpop(final String key) {
        return isCluster ? jedisCluster.rpop(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.rpop(key);
            }
        });
    }

    @Override
    public Long sadd(final String key, final String... member) {
        return isCluster ? jedisCluster.sadd(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sadd(key, member);
                    }
                });
    }

    @Override
    public Set<String> smembers(final String key) {
        return isCluster ? jedisCluster.smembers(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.smembers(key);
                    }
                });
    }

    @Override
    public Long srem(final String key, final String... member) {
        return isCluster ? jedisCluster.srem(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.srem(key, member);
                    }
                });
    }

    @Override
    public String spop(final String key) {
        return isCluster ? jedisCluster.spop(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.spop(key);
            }
        });
    }

    @Override
    public Set<String> spop(final String key, final long count) {
        return isCluster ? jedisCluster.spop(key, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.spop(key, count);
                    }
                });
    }

    @Override
    public Long scard(final String key) {
        return isCluster ? jedisCluster.scard(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    @Override
    public Boolean sismember(final String key, final String member) {
        return isCluster ? jedisCluster.sismember(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Boolean>() {
                    @Override
                    public Boolean action(Jedis jedis) {
                        return jedis.sismember(key, member);
                    }
                });
    }

    @Override
    public String srandmember(final String key) {
        return isCluster ? jedisCluster.srandmember(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.srandmember(key);
                    }
                });
    }

    @Override
    public List<String> srandmember(final String key, final int count) {
        return isCluster ? jedisCluster.srandmember(key, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.srandmember(key, count);
                    }
                });
    }

    @Override
    public Long strlen(final String key) {
        return isCluster ? jedisCluster.strlen(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.strlen(key);
            }
        });
    }

    @Override
    public Long zadd(final String key, final double score, final String member) {
        return isCluster ? jedisCluster.zadd(key, score, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zadd(key, score, member);
                    }
                });
    }

    @Override
    public Long zadd(final String key, final double score, final String member, final ZAddParams params) {
        return isCluster ? jedisCluster.zadd(key, score, member, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zadd(key, score, member, params);
                    }
                });
    }

    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        return isCluster ? jedisCluster.zadd(key, scoreMembers) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zadd(key, scoreMembers);
                    }
                });
    }

    @Override
    public Long zadd(final String key, final Map<String, Double> scoreMembers, final ZAddParams params) {
        return isCluster ? jedisCluster.zadd(key, scoreMembers, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zadd(key, scoreMembers, params);
                    }
                });
    }

    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.zrange(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrange(key, start, end);
                    }
                });
    }

    @Override
    public Long zrem(final String key, final String... member) {
        return isCluster ? jedisCluster.zrem(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zrem(key, member);
                    }
                });
    }

    @Override
    public Double zincrby(final String key, final double score, final String member) {
        return isCluster ? jedisCluster.zincrby(key, score, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.zincrby(key, score, member);
                    }
                });
    }

    @Override
    public Double zincrby(final String key, final double score, final String member, final ZIncrByParams params) {
        return isCluster ? jedisCluster.zincrby(key, score, member, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.zincrby(key, score, member, params);
                    }
                });
    }

    @Override
    public Long zrank(final String key, final String member) {
        return isCluster ? jedisCluster.zrank(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zrank(key, member);
                    }
                });
    }

    @Override
    public Long zrevrank(final String key, final String member) {
        return isCluster ? jedisCluster.zrevrank(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zrevrank(key, member);
                    }
                });
    }

    @Override
    public Set<String> zrevrange(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.zrevrange(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrange(key, start, end);
                    }
                });
    }

    @Override
    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.zrangeWithScores(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrangeWithScores(key, start, end);
                    }
                });
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.zrevrangeWithScores(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrevrangeWithScores(key, start, end);
                    }
                });
    }

    @Override
    public Long zcard(final String key) {
        return isCluster ? jedisCluster.zcard(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    @Override
    public Double zscore(final String key, final String member) {
        return isCluster ? jedisCluster.zscore(key, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.zscore(key, member);
                    }
                });
    }

    @Override
    public List<String> sort(final String key) {
        return isCluster ? jedisCluster.sort(key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.sort(key);
                    }
                });
    }

    @Override
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        return isCluster ? jedisCluster.sort(key, sortingParameters) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.sort(key, sortingParameters);
                    }
                });
    }

    @Override
    public Long zcount(final String key, final double min, final double max) {
        return isCluster ? jedisCluster.zcount(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zcount(key, min, max);
                    }
                });
    }

    @Override
    public Long zcount(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zcount(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zcount(key, min, max);
                    }
                });
    }

    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return isCluster ? jedisCluster.zrangeByScore(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByScore(key, min, max);
                    }
                });
    }

    @Override
    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zrangeByScore(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByScore(key, min, max);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        return isCluster ? jedisCluster.zrevrangeByScore(key, max, min) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByScore(key, max, min);
                    }
                });
    }

    @Override
    public Set<String> zrangeByScore(final String key,
                                     final double min,
                                     final double max,
                                     final int offset,
                                     final int count) {
        return isCluster ? jedisCluster.zrangeByScore(key, min, max, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByScore(key, min, max, offset, count);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        return isCluster ? jedisCluster.zrevrangeByScore(key, max, min) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByScore(key, max, min);
                    }
                });
    }

    @Override
    public Set<String> zrangeByScore(final String key,
                                     final String min,
                                     final String max,
                                     final int offset,
                                     final int count) {
        return isCluster ? jedisCluster.zrangeByScore(key, min, max, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByScore(key, min, max, offset, count);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByScore(final String key,
                                        final double max,
                                        final double min,
                                        final int offset,
                                        final int count) {
        return isCluster ? jedisCluster.zrevrangeByScore(key, max, min, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByScore(key, max, min, offset, count);
                    }
                });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        return isCluster ? jedisCluster.zrangeByScoreWithScores(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrangeByScoreWithScores(key, min, max);
                    }
                });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        return isCluster ? jedisCluster.zrevrangeByScoreWithScores(key, max, min) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrevrangeByScoreWithScores(key, max, min);
                    }
                });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key,
                                              final double min,
                                              final double max,
                                              final int offset,
                                              final int count) {
        return isCluster ? jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByScore(final String key,
                                        final String max,
                                        final String min,
                                        final int offset,
                                        final int count) {
        return isCluster ? jedisCluster.zrevrangeByScore(key, max, min, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByScore(key, max, min, offset, count);
                    }
                });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zrangeByScoreWithScores(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrangeByScoreWithScores(key, min, max);
                    }
                });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max, final String min) {
        return isCluster ? jedisCluster.zrevrangeByScoreWithScores(key, max, min) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrevrangeByScoreWithScores(key, max, min);
                    }
                });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key,
                                              final String min,
                                              final String max,
                                              final int offset,
                                              final int count) {
        return isCluster ? jedisCluster.zrangeByScoreWithScores(key, min, max, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
                    }
                });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key,
                                                 final double max,
                                                 final double min,
                                                 final int offset,
                                                 final int count) {
        return isCluster ? jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset,
                                                                   count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
                    }
                });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key,
                                                 final String max,
                                                 final String min,
                                                 final int offset,
                                                 final int count) {
        return isCluster ? jedisCluster.zrevrangeByScoreWithScores(key, max, min, offset,
                                                                   count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<Tuple>>() {
                    @Override
                    public Set<Tuple> action(Jedis jedis) {
                        return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
                    }
                });
    }

    @Override
    public Long zremrangeByRank(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.zremrangeByRank(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zremrangeByRank(key, start, end);
                    }
                });
    }

    @Override
    public Long zremrangeByScore(final String key, final double start, final double end) {
        return isCluster ? jedisCluster.zremrangeByScore(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zremrangeByScore(key, start, end);
                    }
                });
    }

    @Override
    public Long zremrangeByScore(final String key, final String start, final String end) {
        return isCluster ? jedisCluster.zremrangeByScore(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zremrangeByScore(key, start, end);
                    }
                });
    }

    @Override
    public Long zlexcount(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zlexcount(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zlexcount(key, min, max);
                    }
                });
    }

    @Override
    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zrangeByLex(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByLex(key, min, max);
                    }
                });
    }

    @Override
    public Set<String> zrangeByLex(final String key,
                                   final String min,
                                   final String max,
                                   final int offset,
                                   final int count) {
        return isCluster ? jedisCluster.zrangeByLex(key, min, max, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrangeByLex(key, min, max, offset, count);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByLex(final String key, final String max, final String min) {
        return isCluster ? jedisCluster.zrevrangeByLex(key, max, min) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByLex(key, max, min);
                    }
                });
    }

    @Override
    public Set<String> zrevrangeByLex(final String key,
                                      final String max,
                                      final String min,
                                      final int offset,
                                      final int count) {
        return isCluster ? jedisCluster.zrevrangeByLex(key, max, min, offset, count) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.zrevrangeByLex(key, max, min, offset, count);
                    }
                });
    }

    @Override
    public Long zremrangeByLex(final String key, final String min, final String max) {
        return isCluster ? jedisCluster.zremrangeByLex(key, min, max) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zremrangeByLex(key, min, max);
                    }
                });
    }

    @Override
    public Long linsert(final String key,
                        final BinaryClient.LIST_POSITION where,
                        final String pivot,
                        final String value) {
        return isCluster ? jedisCluster.linsert(key, where, pivot, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.linsert(key, where, pivot, value);
                    }
                });
    }

    @Override
    public Long lpushx(final String key, final String... string) {
        return isCluster ? jedisCluster.lpushx(key, string) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.lpushx(key, string);
                    }
                });
    }

    @Override
    public Long rpushx(final String key, final String... string) {
        return isCluster ? jedisCluster.rpushx(key, string) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.rpushx(key, string);
                    }
                });
    }

    @Override
    @Deprecated
    public List<String> blpop(final String arg) {
        return isCluster ? jedisCluster.blpop(arg) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.blpop(arg);
                    }
                });
    }

    @Override
    public List<String> blpop(final int timeout, final String key) {
        return isCluster ? jedisCluster.blpop(timeout, key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.blpop(timeout, key);
                    }
                });
    }

    @Override
    @Deprecated
    public List<String> brpop(final String arg) {
        return isCluster ? jedisCluster.brpop(arg) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.brpop(arg);
                    }
                });
    }

    @Override
    public List<String> brpop(final int timeout, final String key) {
        return isCluster ? jedisCluster.brpop(timeout, key) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.brpop(timeout, key);
                    }
                });
    }

    @Override
    public Long del(final String key) {
        return isCluster ? jedisCluster.del(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.del(key);
            }
        });
    }

    @Override
    public String echo(final String string) {
        return isCluster ? jedisCluster.echo(string) : jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.echo(string);
            }
        });
    }

    @Override
    public Long move(final String key, final int dbIndex) {
        return isCluster ? jedisCluster.move(key, dbIndex) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.move(key, dbIndex);
                    }
                });
    }

    @Override
    public Long bitcount(final String key) {
        return isCluster ? jedisCluster.bitcount(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.bitcount(key);
            }
        });
    }

    @Override
    public Long bitcount(final String key, final long start, final long end) {
        return isCluster ? jedisCluster.bitcount(key, start, end) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.bitcount(key, start, end);
                    }
                });
    }

    @Override
    public Long bitpos(final String key, final boolean value) {
        return isCluster ? jedisCluster.bitpos(key, value) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.bitpos(key, value);
                    }
                });
    }

    @Override
    public Long bitpos(final String key, final boolean value, final BitPosParams params) {
        return isCluster ? jedisCluster.bitpos(key, value, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.bitpos(key, value, params);
                    }
                });
    }

    @Override
    @Deprecated
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final int cursor) {
        return isCluster ? jedisCluster.hscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Map.Entry<String, String>>>() {
                    @Override
                    public ScanResult<Map.Entry<String, String>> action(Jedis jedis) {
                        return jedis.hscan(key, cursor);
                    }
                });
    }

    @Override
    @Deprecated
    public ScanResult<String> sscan(final String key, final int cursor) {
        return isCluster ? jedisCluster.sscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<String>>() {
                    @Override
                    public ScanResult<String> action(Jedis jedis) {
                        return jedis.sscan(key, cursor);
                    }
                });
    }

    @Override
    @Deprecated
    public ScanResult<Tuple> zscan(final String key, final int cursor) {
        return isCluster ? jedisCluster.zscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Tuple>>() {
                    @Override
                    public ScanResult<Tuple> action(Jedis jedis) {
                        return jedis.zscan(key, cursor);
                    }
                });
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor) {
        return isCluster ? jedisCluster.hscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Map.Entry<String, String>>>() {
                    @Override
                    public ScanResult<Map.Entry<String, String>> action(Jedis jedis) {
                        return jedis.hscan(key, cursor);
                    }
                });
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor, final ScanParams params) {
        return isCluster ? jedisCluster.hscan(key, cursor, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Map.Entry<String, String>>>() {
                    @Override
                    public ScanResult<Map.Entry<String, String>> action(Jedis jedis) {
                        return jedis.hscan(key, cursor, params);
                    }
                });
    }

    @Override
    public ScanResult<String> sscan(final String key, final String cursor) {
        return isCluster ? jedisCluster.sscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<String>>() {
                    @Override
                    public ScanResult<String> action(Jedis jedis) {
                        return jedis.sscan(key, cursor);
                    }
                });
    }

    @Override
    public ScanResult<String> sscan(final String key, final String cursor, final ScanParams params) {
        return isCluster ? jedisCluster.sscan(key, cursor, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<String>>() {
                    @Override
                    public ScanResult<String> action(Jedis jedis) {
                        return jedis.sscan(key, cursor, params);
                    }
                });
    }

    @Override
    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        return isCluster ? jedisCluster.zscan(key, cursor) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Tuple>>() {
                    @Override
                    public ScanResult<Tuple> action(Jedis jedis) {
                        return jedis.zscan(key, cursor);
                    }
                });
    }

    @Override
    public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params) {
        return isCluster ? jedisCluster.zscan(key, cursor, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<Tuple>>() {
                    @Override
                    public ScanResult<Tuple> action(Jedis jedis) {
                        return jedis.zscan(key, cursor, params);
                    }
                });
    }

    @Override
    public Long pfadd(final String key, final String... elements) {
        return isCluster ? jedisCluster.pfadd(key, elements) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.pfadd(key, elements);
                    }
                });
    }

    @Override
    public long pfcount(final String key) {
        return isCluster ? jedisCluster.pfcount(key) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.pfcount(key);
            }
        });
    }

    @Override
    public Long geoadd(final String key, final double longitude, final double latitude, final String member) {
        return isCluster ? jedisCluster.geoadd(key, longitude, latitude, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.geoadd(key, longitude, latitude, member);
                    }
                });
    }

    @Override
    public Long geoadd(final String key, final Map<String, GeoCoordinate> memberCoordinateMap) {
        return isCluster ? jedisCluster.geoadd(key, memberCoordinateMap) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.geoadd(key, memberCoordinateMap);
                    }
                });
    }

    @Override
    public Double geodist(final String key, final String member1, final String member2) {
        return isCluster ? jedisCluster.geodist(key, member1, member2) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.geodist(key, member1, member2);
                    }
                });
    }

    @Override
    public Double geodist(final String key, final String member1, final String member2, final GeoUnit unit) {
        return isCluster ? jedisCluster.geodist(key, member1, member2, unit) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Double>() {
                    @Override
                    public Double action(Jedis jedis) {
                        return jedis.geodist(key, member1, member2, unit);
                    }
                });
    }

    @Override
    public List<String> geohash(final String key, final String... members) {
        return isCluster ? jedisCluster.geohash(key, members) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.geohash(key, members);
                    }
                });
    }

    @Override
    public List<GeoCoordinate> geopos(final String key, final String... members) {
        return isCluster ? jedisCluster.geopos(key, members) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<GeoCoordinate>>() {
                    @Override
                    public List<GeoCoordinate> action(Jedis jedis) {
                        return jedis.geopos(key, members);
                    }
                });
    }

    @Override
    public List<GeoRadiusResponse> georadius(final String key,
                                             final double longitude,
                                             final double latitude,
                                             final double radius,
                                             final GeoUnit unit) {
        return isCluster ? jedisCluster.georadius(key, longitude, latitude, radius, unit) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<GeoRadiusResponse>>() {
                    @Override
                    public List<GeoRadiusResponse> action(Jedis jedis) {
                        return jedis.georadius(key, longitude, latitude, radius, unit);
                    }
                });
    }

    @Override
    public List<GeoRadiusResponse> georadius(final String key,
                                             final double longitude,
                                             final double latitude,
                                             final double radius,
                                             final GeoUnit unit,
                                             final GeoRadiusParam param) {
        return isCluster ? jedisCluster.georadius(key, longitude, latitude, radius, unit,
                                                  param) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<GeoRadiusResponse>>() {
                    @Override
                    public List<GeoRadiusResponse> action(Jedis jedis) {
                        return jedis.georadius(key, longitude, latitude, radius, unit, param);
                    }
                });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(final String key,
                                                     final String member,
                                                     final double radius,
                                                     final GeoUnit unit) {
        return isCluster ? jedisCluster.georadiusByMember(key, member, radius, unit) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<GeoRadiusResponse>>() {
                    @Override
                    public List<GeoRadiusResponse> action(Jedis jedis) {
                        return jedis.georadiusByMember(key, member, radius, unit);
                    }
                });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(final String key,
                                                     final String member,
                                                     final double radius,
                                                     final GeoUnit unit,
                                                     final GeoRadiusParam param) {
        return isCluster ? jedisCluster.georadiusByMember(key, member, radius, unit, param) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<GeoRadiusResponse>>() {
                    @Override
                    public List<GeoRadiusResponse> action(Jedis jedis) {
                        return jedis.georadiusByMember(key, member, radius, unit, param);
                    }
                });
    }

    @Override
    public List<Long> bitfield(final String key, final String... arguments) {
        return isCluster ? jedisCluster.bitfield(key, arguments) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<Long>>() {
                    @Override
                    public List<Long> action(Jedis jedis) {
                        return jedis.bitfield(key, arguments);
                    }
                });
    }
    // endregion

    // region [MultiKeyJedisClusterCommands & MultiKeyCommands] methods
    @Override
    public Long exists(final String... keys) {
        return isCluster ? jedisCluster.exists(keys) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.exists(keys);
            }
        });
    }

    @Override
    public Long del(final String... keys) {
        return isCluster ? jedisCluster.del(keys) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.del(keys);
            }
        });
    }

    @Override
    public List<String> blpop(final int timeout, final String... keys) {
        return isCluster ? jedisCluster.blpop(timeout, keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.blpop(timeout, keys);
                    }
                });
    }

    @Override
    public List<String> brpop(final int timeout, final String... keys) {
        return isCluster ? jedisCluster.brpop(timeout, keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.brpop(timeout, keys);
                    }
                });
    }

    @Override
    public List<String> mget(final String... keys) {
        return isCluster ? jedisCluster.mget(keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<List<String>>() {
                    @Override
                    public List<String> action(Jedis jedis) {
                        return jedis.mget(keys);
                    }
                });
    }

    @Override
    public String mset(final String... keysvalues) {
        return isCluster ? jedisCluster.mset(keysvalues) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.mset(keysvalues);
                    }
                });
    }

    @Override
    public Long msetnx(final String... keysvalues) {
        return isCluster ? jedisCluster.msetnx(keysvalues) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.msetnx(keysvalues);
                    }
                });
    }

    @Override
    public String rename(final String oldkey, final String newkey) {
        return isCluster ? jedisCluster.rename(oldkey, newkey) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.rename(oldkey, newkey);
                    }
                });
    }

    @Override
    public Long renamenx(final String oldkey, final String newkey) {
        return isCluster ? jedisCluster.renamenx(oldkey, newkey) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.renamenx(oldkey, newkey);
                    }
                });
    }

    @Override
    public String rpoplpush(final String srckey, final String dstkey) {
        return isCluster ? jedisCluster.rpoplpush(srckey, dstkey) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.rpoplpush(srckey, dstkey);
                    }
                });
    }

    @Override
    public Set<String> sdiff(final String... keys) {
        return isCluster ? jedisCluster.sdiff(keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.sdiff(keys);
                    }
                });
    }

    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        return isCluster ? jedisCluster.sdiffstore(dstkey, keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sdiffstore(dstkey, keys);
                    }
                });
    }

    @Override
    public Set<String> sinter(final String... keys) {
        return isCluster ? jedisCluster.sinter(keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.sinter(keys);
                    }
                });
    }

    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        return isCluster ? jedisCluster.sinterstore(dstkey, keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sinterstore(dstkey, keys);
                    }
                });
    }

    @Override
    public Long smove(final String srckey, final String dstkey, final String member) {
        return isCluster ? jedisCluster.smove(srckey, dstkey, member) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.smove(srckey, dstkey, member);
                    }
                });
    }

    @Override
    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        return isCluster ? jedisCluster.sort(key, sortingParameters, dstkey) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sort(key, sortingParameters, dstkey);
                    }
                });
    }

    @Override
    public Long sort(final String key, final String dstkey) {
        return isCluster ? jedisCluster.sort(key, dstkey) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sort(key, dstkey);
                    }
                });
    }

    @Override
    public Set<String> sunion(final String... keys) {
        return isCluster ? jedisCluster.sunion(keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Set<String>>() {
                    @Override
                    public Set<String> action(Jedis jedis) {
                        return jedis.sunion(keys);
                    }
                });
    }

    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        return isCluster ? jedisCluster.sunionstore(dstkey, keys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.sunionstore(dstkey, keys);
                    }
                });
    }

    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        return isCluster ? jedisCluster.zinterstore(dstkey, sets) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zinterstore(dstkey, sets);
                    }
                });
    }

    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        return isCluster ? jedisCluster.zinterstore(dstkey, params, sets) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zinterstore(dstkey, params, sets);
                    }
                });
    }

    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        return isCluster ? jedisCluster.zunionstore(dstkey, sets) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zunionstore(dstkey, sets);
                    }
                });
    }

    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        return isCluster ? jedisCluster.zunionstore(dstkey, params, sets) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.zunionstore(dstkey, params, sets);
                    }
                });
    }

    @Override
    public String brpoplpush(final String source, final String destination, final int timeout) {
        return isCluster ? jedisCluster.brpoplpush(source, destination, timeout) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.brpoplpush(source, destination, timeout);
                    }
                });
    }

    @Override
    public Long publish(final String channel, final String message) {
        return isCluster ? jedisCluster.publish(channel, message) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.publish(channel, message);
                    }
                });
    }

    @Override
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        if (isCluster)
            jedisCluster.subscribe(jedisPubSub, channels);
        else
            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    jedis.subscribe(jedisPubSub, channels);
                }
            });
    }

    @Override
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        if (isCluster)
            jedisCluster.psubscribe(jedisPubSub, patterns);
        else
            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                @Override
                public void action(Jedis jedis) {
                    jedis.psubscribe(jedisPubSub, patterns);
                }
            });
    }

    @Override
    public Long bitop(final BitOP op, final String destKey, final String... srcKeys) {
        return isCluster ? jedisCluster.bitop(op, destKey, srcKeys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Long>() {
                    @Override
                    public Long action(Jedis jedis) {
                        return jedis.bitop(op, destKey, srcKeys);
                    }
                });
    }

    @Override
    public String pfmerge(final String destkey, final String... sourcekeys) {
        return isCluster ? jedisCluster.pfmerge(destkey, sourcekeys) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<String>() {
                    @Override
                    public String action(Jedis jedis) {
                        return jedis.pfmerge(destkey, sourcekeys);
                    }
                });
    }

    @Override
    public long pfcount(final String... keys) {
        return isCluster ? jedisCluster.pfcount(keys) : jedisTemplate.execute(new JedisTemplate.JedisAction<Long>() {
            @Override
            public Long action(Jedis jedis) {
                return jedis.pfcount(keys);
            }
        });
    }

    @Override
    public ScanResult<String> scan(final String cursor, final ScanParams params) {
        return isCluster ? jedisCluster.scan(cursor, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<ScanResult<String>>() {
                    @Override
                    public ScanResult<String> action(Jedis jedis) {
                        return jedis.scan(cursor, params);
                    }
                });
    }
    // endregion

    // region [MultiKeyCommands] methods
    @Override
    public List<String> blpop(final String... args) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<List<String>>() {
            @Override
            public List<String> action(Jedis jedis) {
                return jedis.blpop(args);
            }
        });
    }

    @Override
    public List<String> brpop(final String... args) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<List<String>>() {
            @Override
            public List<String> action(Jedis jedis) {
                return jedis.brpop(args);
            }
        });
    }

    @Override
    public Set<String> keys(final String pattern) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<Set<String>>() {
            @Override
            public Set<String> action(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    @Override
    public String watch(final String... keys) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.watch(keys);
            }
        });
    }

    @Override
    public String unwatch() {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.unwatch();
            }
        });
    }

    @Override
    public String randomKey() {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.randomKey();
            }
        });
    }

    @Override
    @Deprecated
    public ScanResult<String> scan(final int cursor) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<ScanResult<String>>() {
            @Override
            public ScanResult<String> action(Jedis jedis) {
                return jedis.scan(cursor);
            }
        });
    }

    @Override
    public ScanResult<String> scan(final String cursor) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<ScanResult<String>>() {
            @Override
            public ScanResult<String> action(Jedis jedis) {
                return jedis.scan(cursor);
            }
        });
    }
    // endregion

    // region [JedisClusterScriptingCommands & ScriptingCommands] methods
    @Override
    public Object eval(final String script, final int keyCount, final String... params) {
        return isCluster ? jedisCluster.eval(script, keyCount, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Object>() {
                    @Override
                    public Object action(Jedis jedis) {
                        return jedis.eval(script, keyCount, params);
                    }
                });
    }

    @Override
    public Object eval(final String script, final List<String> keys, final List<String> args) {
        return isCluster ? jedisCluster.eval(script, keys, args) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Object>() {
                    @Override
                    public Object action(Jedis jedis) {
                        return jedis.eval(script, keys, args);
                    }
                });
    }

    @Override
    public Object evalsha(final String sha1, final List<String> keys, final List<String> args) {
        return isCluster ? jedisCluster.evalsha(sha1, keys, args) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Object>() {
                    @Override
                    public Object action(Jedis jedis) {
                        return jedis.evalsha(sha1, keys, args);
                    }
                });
    }

    @Override
    public Object evalsha(final String sha1, final int keyCount, final String... params) {
        return isCluster ? jedisCluster.evalsha(sha1, keyCount, params) : jedisTemplate.execute(
                new JedisTemplate.JedisAction<Object>() {
                    @Override
                    public Object action(Jedis jedis) {
                        return jedis.evalsha(sha1, keyCount, params);
                    }
                });
    }
    // endregion

    // region [JedisClusterScriptingCommands] methods
    @Override
    public Object eval(String script, String key) {
        return getJedisCluster().eval(script, key);
    }

    @Override
    public Object evalsha(String script, String key) {
        return getJedisCluster().evalsha(script, key);
    }

    @Override
    public Boolean scriptExists(String sha1, String key) {
        return getJedisCluster().scriptExists(sha1, key);
    }

    @Override
    public List<Boolean> scriptExists(String key, String... sha1) {
        return getJedisCluster().scriptExists(key, sha1);
    }

    @Override
    public String scriptLoad(String script, String key) {
        return getJedisCluster().scriptLoad(script, key);
    }
    // endregion

    // region [ScriptingCommands] methods
    @Override
    public Object eval(final String script) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<Object>() {
            @Override
            public Object action(Jedis jedis) {
                return jedis.eval(script);
            }
        });
    }

    @Override
    public Object evalsha(final String script) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<Object>() {
            @Override
            public Object action(Jedis jedis) {
                return jedis.evalsha(script);
            }
        });
    }

    @Override
    public Boolean scriptExists(final String sha1) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<Boolean>() {
            @Override
            public Boolean action(Jedis jedis) {
                return jedis.scriptExists(sha1);
            }
        });
    }

    @Override
    public List<Boolean> scriptExists(final String... sha1) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<List<Boolean>>() {
            @Override
            public List<Boolean> action(Jedis jedis) {
                return jedis.scriptExists(sha1);
            }
        });
    }

    @Override
    public String scriptLoad(final String script) {
        return getJedisTemplate().execute(new JedisTemplate.JedisAction<String>() {
            @Override
            public String action(Jedis jedis) {
                return jedis.scriptLoad(script);
            }
        });
    }
    //endregion

    // region [InitializingBean] methods
    @Override
    public void afterPropertiesSet() throws Exception {
        if (jedisCluster == null && jedisTemplate == null) {
            throw new IllegalArgumentException("Jedis or JedisCluster expected");
        }

        // cluster mode first
        if (jedisCluster != null) {
            isCluster = true;
        } else {
            isCluster = false;
        }
    }
    // endregion
}
