package com.neusoft.features.session;

import com.neusoft.features.common.utils.SpringContextHolder;
import com.neusoft.features.redis.JedisAdapter;
import com.neusoft.features.session.exception.SessionException;
import com.neusoft.features.session.redis.JsonSerializer;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * 分布式session管理器。
 * <p/>
 * 负责session CRUD操作。
 *
 * @author andy.jiao@msn.com
 */
//public class DistributedSessionManager extends RedisClusterDao<String> implements SessionManager {
public class DistributedSessionManager implements SessionManager {

    private static final Logger log = LoggerFactory.getLogger(DistributedSessionManager.class);

    /**
     * session在缓存中的前缀
     */
    private final String sessionPrefix;

    /**
     * session生成器
     */
    private final SessionIdGenerator sessionIdGenerator;

    /**
     * 序列化
     */
    private final JsonSerializer redisSerializer;

    /**
     * jedis cluster
     */
    private final JedisAdapter jedisCluster;
//    /**
//     * jedis
//     */
//    private volatile JedisPoolExecutor executor;

    /**
     * 构造方法。
     *
     * 读取session.properties。
     */
    private DistributedSessionManager() {

        Properties properties = new Properties();
        InputStream input = null;
        try {
            // input = (InputStream) Resources.newInputStreamSupplier(Resources.getResource("session.properties")).getInput();
            input = this.getClass().getClassLoader().getResourceAsStream("config/session.properties");
            properties.load(input);
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e) {
            log.error("failed to load session.properties", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }

        this.sessionIdGenerator = new DefaultSessionIdGenerator();
        this.redisSerializer = new JsonSerializer();
        // session前缀，每个web app都应不同
        this.sessionPrefix = properties.getProperty("session.redis.prefix", "ecsession");
        // TODO:考虑单机模式，改为使用JedisAdapter，发布时改为JedisCluster
        // 获取jedis cluster实例
//        this.jedisCluster = SpringContextHolder.getBean(JedisCluster.class);
        this.jedisCluster = SpringContextHolder.getBean(JedisAdapter.class);
    }

    public static DistributedSessionManager instance() {
        return SingletonHolder.instance;
    }

    public SessionIdGenerator getSessionIdGenerator() {
        return this.sessionIdGenerator;
    }

    public Map<String, Object> findSessionById(String id) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            String session = jedisCluster.get(sessionId);
            if (Strings.isNullOrEmpty(session)) {
                return Collections.emptyMap();
            } else {
                return DistributedSessionManager.this.redisSerializer.deserialize(session);
            }
        } catch (Exception e) {
            log.error("failed to find session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
            throw new SessionException("get session failed", e);
        }
    }

    public void refreshExpireTime(DistributedSession afSession, final int maxInactiveInterval) {
        final String sessionId = this.sessionPrefix + ":" + afSession.getId();
        try {
            jedisCluster.expire(sessionId, maxInactiveInterval);
        } catch (Exception e) {
            log.error("failed to refresh expire time session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
    }

    public void deletePhysically(String id) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            jedisCluster.del(sessionId);
        } catch (Exception e) {
            log.error("failed to delete session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
    }

    public boolean save(String id, final Map<String, Object> snapshot, final int maxInactiveInterval) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            if (snapshot.isEmpty()) {
                jedisCluster.del(sessionId);
            } else {
                jedisCluster.setex(sessionId, maxInactiveInterval, DistributedSessionManager.this.redisSerializer.serialize(snapshot));
            }
            return true;
        } catch (Exception e) {
            log.error("failed to delete session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    public void destroy() {
//        if (this.executor != null) {
//            this.executor.getJedisPool().destroy();
//        }
    }

    static class SingletonHolder {
        static DistributedSessionManager instance = new DistributedSessionManager();
    }
}