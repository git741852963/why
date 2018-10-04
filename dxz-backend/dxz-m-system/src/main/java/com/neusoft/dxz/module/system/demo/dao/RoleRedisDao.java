package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.dxz.module.system.demo.constants.SystemRedisKeyConstants;
import com.neusoft.features.redis.dao.RedisClusterDao;
import org.springframework.stereotype.Repository;

/**
 * Role Redis Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class RoleRedisDao extends RedisClusterDao<Role> {


    /**
     * 取得角色详细信息(包含权限信息)
     *
     * @param id 角色id
     * @return 角色详细信息(包含权限信息)
     */
    public Role findRoleDetailInfo(String id) {
        return fromHashField(SystemRedisKeyConstants.ROLE_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ROLE_DETAIL_INFO + id);
    }

    /**
     * 保存角色详细信息(包含权限信息)
     *
     * @param id   角色id
     * @param role 角色详细信息(包含权限信息)
     */
    public void setRoleDetailInfo(String id, Role role) {
        toHashField(SystemRedisKeyConstants.ROLE_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ROLE_DETAIL_INFO + id, role);
    }

    /**
     * 取得默认的角色(商家、平台商家、会员)
     *
     * @param key 默认类型key
     * @return 默认的角色
     */
    public Role findDefaultRole(String key) {
        return fromHashField(SystemRedisKeyConstants.ROLE_CACHE_COLLECTION, SystemRedisKeyConstants.GET_DEFAULT_ROLE_INFO + key);
    }

    /**
     * 保存取得默认的角色(商家、平台商家、会员)
     *
     * @param key  默认类型key
     * @param role 默认的角色
     */
    public void setDefaultRole(String key, Role role) {
        toHashField(SystemRedisKeyConstants.ROLE_CACHE_COLLECTION, SystemRedisKeyConstants.GET_DEFAULT_ROLE_INFO + key, role);
    }

    /**
     * 删除所有角色相关的缓存数据
     */
    public void clear() {
        this.jedisAdapter.del(SystemRedisKeyConstants.ROLE_CACHE_COLLECTION);
    }
}
