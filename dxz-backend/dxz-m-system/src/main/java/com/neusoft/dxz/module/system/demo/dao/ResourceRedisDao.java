package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Resource;
import com.neusoft.dxz.module.system.demo.constants.SystemRedisKeyConstants;
import com.neusoft.features.redis.dao.RedisClusterDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuthorityRedisDao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class ResourceRedisDao extends RedisClusterDao<Resource> {

    /**
     * 根据级别和类型取得权限数据
     *
     * @param key 级别和类型Key
     * @return 权限数据
     */
    public List<Resource> findResourcesByCategory(String key) {
        return fromHashFieldToList(SystemRedisKeyConstants.AUTHORITY_CACHE_COLLECTION, SystemRedisKeyConstants.GET_CATEGORY_AUTHORITY_INFO + key);
    }

    /**
     * 根据级别和类型保存权限数据
     *
     * @param key       级别和类型Key
     * @param resources 权限数据
     */
    public void setResourcesByCategory(String key, List<Resource> resources) {
        toHashField(SystemRedisKeyConstants.AUTHORITY_CACHE_COLLECTION, SystemRedisKeyConstants.GET_CATEGORY_AUTHORITY_INFO + key, resources);
    }

    /**
     * 根据角色和类型取得权限数据
     *
     * @param key 角色和类型Key
     * @return 权限数据
     */
    public List<Resource> findResourcesByRoleId(String key) {
        return fromHashFieldToList(SystemRedisKeyConstants.AUTHORITY_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ROLE_AUTHORITY_INFO + key);
    }

    /**
     * 根据角色和类型保存权限数据
     *
     * @param key         角色和类型Key
     * @param authorities 权限数据
     */
    public void setResourcesByRoleId(String key, List<Resource> authorities) {
        toHashField(SystemRedisKeyConstants.AUTHORITY_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ROLE_AUTHORITY_INFO + key, authorities);
    }

    /**
     * 删除所有权限相关的缓存数据
     */
    public void clear() {
        this.jedisAdapter.del(SystemRedisKeyConstants.AUTHORITY_CACHE_COLLECTION);
    }
}
