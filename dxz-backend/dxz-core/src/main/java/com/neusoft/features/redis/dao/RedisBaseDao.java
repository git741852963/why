package com.neusoft.features.redis.dao;

import com.neusoft.features.redis.utils.JedisTemplate;
import com.neusoft.features.redis.utils.JedisTemplate.JedisAction;
import com.neusoft.features.redis.utils.KeyUtils;
import com.neusoft.features.redis.utils.StringHashMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * RedisDao基类，用于单机redis或proxy/sentinel集群。
 * <p/>
 * 框架改用RedisCluster,已废弃。
 *
 * @param <T> 泛型T
 */
@Deprecated
public abstract class RedisBaseDao<T> {
    public final StringHashMapper<T> stringHashMapper;
    protected final JedisTemplate template;
    protected final Class<T> entityClass;

    public RedisBaseDao(JedisTemplate template) {
        this.template = template;
        this.entityClass = ((Class) ((java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        this.stringHashMapper = new StringHashMapper(this.entityClass);
    }

    public List<T> findByIds(final Iterable<String> ids) {
        if (Iterables.isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<Response<Map<String, String>>> result = (List) this.template.execute(new JedisAction() {
            public List<Response<Map<String, String>>> action(Jedis jedis) {
                List<Response<Map<String, String>>> result = Lists.newArrayListWithCapacity(Iterables.size(ids));
                Pipeline p = jedis.pipelined();
                for (String id : ids) {
                    result.add(p.hgetAll(KeyUtils.entityId(RedisBaseDao.this.entityClass, id)));
                }
                p.sync();
                return result;
            }
        });
        List<T> entities = Lists.newArrayListWithCapacity(result.size());
        for (Response<Map<String, String>> mapResponse : result) {
            entities.add(this.stringHashMapper.fromHash(mapResponse.get()));
        }
        return entities;
    }

    protected T findByKey(final Long id) {
        Map<String, String> hash = (Map) this.template.execute(new JedisAction() {
            public Map<String, String> action(Jedis jedis) {
                return jedis.hgetAll(KeyUtils.entityId(RedisBaseDao.this.entityClass, id.longValue()));
            }
        });
        return this.stringHashMapper.fromHash(hash);
    }

    public Long newId() {
        return (Long) this.template.execute(new JedisAction() {
            public Long action(Jedis jedis) {
                return jedis.incr(KeyUtils.entityCount(RedisBaseDao.this.entityClass));
            }
        });
    }
}