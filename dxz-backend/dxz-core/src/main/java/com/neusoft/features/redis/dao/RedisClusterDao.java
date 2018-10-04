package com.neusoft.features.redis.dao;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.features.redis.JedisAdapter;
import com.neusoft.features.redis.utils.StringHashMapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.neusoft.features.common.utils.Arguments;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis Cluster Dao。
 *
 * 用于访问cluster模式的redis集群。
 *
 * @author andy.jiao@msn.com
 */
public abstract class RedisClusterDao<T> {

    /** redis cluster 客户端 */
    @Autowired
    protected JedisAdapter jedisAdapter;

    /** json转换器 */
    protected StringHashMapper<T> stringHashMapper;

    /** 对应实体类 */
    protected final Class<T> entityClass;

    /**
     * 构造函数
     */
    public RedisClusterDao() {
        this.entityClass = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        this.stringHashMapper = new StringHashMapper(this.entityClass);
    }

    /**
     * 读取hash,并转换为java object
     *
     * @param key hash key
     * @return java object
     */
    protected T fromHash(String key) {
        Map<String, String> result = jedisAdapter.hgetAll(key);
        return result == null ? null : this.stringHashMapper.fromHash(result);
    }

    /**
     * 读取hash,将每个field转换为java object
     *
     * @param key hash key
     * @return java object list
     */
    protected List<T> fromHashFields(String key) {
        Map<String, String> map = jedisAdapter.hgetAll(key);
        List<T> list = null;
        if (map != null && map.entrySet().size() > 0) {
            list = Lists.newArrayListWithExpectedSize(map.entrySet().size());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(JSONObject.parseObject(entry.getValue(), RedisClusterDao.this.entityClass));
            }
        }
        return list;
    }

    /**
     * 读取hash的某个List类型的的field,将value转换为List
     *
     * @param hash hash key
     * @param key hash field
     * @return java object list
     */
    protected List<T> fromHashFieldToList(String hash, String key) {
        String json = this.jedisAdapter.hget(hash, key);
        return json == null ? null : JSONObject.parseArray(json, RedisClusterDao.this.entityClass);
    }

    /**
     * 读取hash的某个List类型的的field,将value转换为Map
     *
     * @param hash hash key
     * @param key hash field
     * @return java object list
     */
    protected HashMap<String, Object> fromHashFieldToMap(String hash, String key) {
        String json = this.jedisAdapter.hget(hash, key);
        return json == null ? null : JSONObject.parseObject(json, HashMap.class);
    }

    /**
     * 从hash获取具体field,并转换为java object
     *
     * @param key hash key
     * @param field hash field
     * @return java object
     */
    protected T fromHashField(String key, String field) {
        String result = jedisAdapter.hget(key, field);
        return result == null ? null : JSONObject.parseObject(jedisAdapter.hget(key, field), RedisClusterDao.this.entityClass);
    }

    /**
     * 将java object保存到hash的field中
     *
     * @param key hash key
     * @param field hash field
     * @param object java object
     */
    protected void toHashField(String key, String field, Object object) {
        String json = JSONObject.toJSONString(object);
        jedisAdapter.hset(key, field, json);
    }

    /**
     * 获取entity count key
     *
     * @return entity count key
     */
    protected String entityCountKey() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, RedisClusterDao.this.entityClass.getSimpleName()) + ":count";
    }

    /**
     * 获取entity count key
     *
     * @param id entity id
     * @return entity id key
     */
    protected String entityIdKey(Long id) {
        Preconditions.checkArgument(Arguments.notNull(id), "id can not be null");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, RedisClusterDao.this.entityClass.getSimpleName()) + ":" + id;
    }

    /**
     * 获取entity count key
     *
     * @param prefix entity prefix
     * @param id entity id
     * @return entity id key
     */
    protected String entityIdKey(String prefix, Long id) {
        Preconditions.checkArgument(Arguments.notNull(id), "id can not be null");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, prefix) + ":" + id;
    }

    /**
     * 获取entity count key
     *
     * @param id entity id
     * @return entity id key
     */
    protected String entityIdKey(String id) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "id can not be null");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, RedisClusterDao.this.entityClass.getSimpleName()) + ":" + id;
    }

    /**
     * 获取自增ID
     *
     * @return 自增Id
     */
    public Long newId() {
        return jedisAdapter.incr(entityCountKey());
    }
}
