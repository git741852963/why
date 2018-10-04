package com.neusoft.features.session;

import java.util.Map;

/**
 * session管理接口。
 *
 * @author andy.jiao@msn.com
 */
public interface SessionManager {

    /**
     * 获取session id生成器
     */
    SessionIdGenerator getSessionIdGenerator();

    /**
     * 根据id查找session
     */
    Map<String, Object> findSessionById(String id);

    /**
     * 刷新过期时间
     */
    void refreshExpireTime(DistributedSession afSession, final int maxInactiveInterval);

    /**
     * 物理删除
     */
    void deletePhysically(String id);

    /**
     * 保存cookie
     */
    boolean save(String id, final Map<String, Object> snapshot, final int maxInactiveInterval);

    /**
     * 销毁session管理器
     */
    void destroy();
}
