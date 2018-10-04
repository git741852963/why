package com.neusoft.features.session;

import com.neusoft.features.session.exception.SessionException;
import com.neusoft.features.session.redis.JedisCallback;
import com.neusoft.features.session.redis.JedisPoolExecutor;
import com.neusoft.features.session.redis.JsonSerializer;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

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
@Deprecated
public class OldDistributedSessionManager implements SessionManager {

    private static final Logger log = LoggerFactory.getLogger(OldDistributedSessionManager.class);

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
     * jedis
     */
    private volatile JedisPoolExecutor executor;

    /**
     * 构造方法。
     *
     * 读取session.properties。
     */
    private OldDistributedSessionManager() {
        this.sessionIdGenerator = new DefaultSessionIdGenerator();

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
        this.redisSerializer = new JsonSerializer();
        this.sessionPrefix = properties.getProperty("session.redis.prefix", "ecsession");

        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setMaxIdle(Integer.parseInt(properties.getProperty("session.redis.pool.maxIdle", "2")));
        config.setMaxTotal(Integer.parseInt(properties.getProperty("session.redis.pool.maxTotal", "5")));

        String mode = properties.getProperty("session.redis.mode");
        if (Objects.equal(mode, "cluster")) {
            this.executor = new JedisPoolExecutor(config, true, properties);
        } else {
            this.executor = new JedisPoolExecutor(config, false, properties);
        }
    }

    public static OldDistributedSessionManager instance() {
        return SingletonHolder.instance;
    }

    public SessionIdGenerator getSessionIdGenerator() {
        return this.sessionIdGenerator;
    }

    public Map<String, Object> findSessionById(String id) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            return (Map) this.executor.execute(new JedisCallback() {
                public Map<String, Object> execute(Jedis jedis) {
                    String session = jedis.get(sessionId);
                    if (!Strings.isNullOrEmpty(session)) {
                        return OldDistributedSessionManager.this.redisSerializer.deserialize(session);
                    }
                    return Collections.emptyMap();
                }
            });
        } catch (Exception e) {
            log.error("failed to find session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
            throw new SessionException("get session failed", e);
        }
    }

    public void refreshExpireTime(DistributedSession afSession, final int maxInactiveInterval) {
        final String sessionId = this.sessionPrefix + ":" + afSession.getId();
        try {
            this.executor.execute(new JedisCallback() {
                public Void execute(Jedis jedis) {
                    jedis.expire(sessionId, maxInactiveInterval);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("failed to refresh expire time session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
    }

    public void deletePhysically(String id) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            this.executor.execute(new JedisCallback() {
                public Void execute(Jedis jedis) {
                    jedis.del(sessionId);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("failed to delete session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
    }

    public boolean save(String id, final Map<String, Object> snapshot, final int maxInactiveInterval) {
        final String sessionId = this.sessionPrefix + ":" + id;
        try {
            this.executor.execute(new JedisCallback() {
                public Void execute(Jedis jedis) {
                    if (snapshot.isEmpty()) {
                        jedis.del(sessionId);
                    } else {
                        jedis.setex(sessionId, maxInactiveInterval, OldDistributedSessionManager.this.redisSerializer.serialize(snapshot));
                    }
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            log.error("failed to delete session(key={}) in redis,cause:{}", sessionId, Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    public void destroy() {
        if (this.executor != null) {
            this.executor.getJedisPool().destroy();
        }
    }

    static class SingletonHolder {
        static OldDistributedSessionManager instance = new OldDistributedSessionManager();
    }
}