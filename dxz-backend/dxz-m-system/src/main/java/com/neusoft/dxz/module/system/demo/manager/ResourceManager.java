package com.neusoft.dxz.module.system.demo.manager;

import com.neusoft.dxz.module.system.demo.dao.ResourceRedisDao;
import com.neusoft.dxz.module.system.demo.dao.ResourceDao;
import com.neusoft.dxz.module.system.demo.dao.RoleResourceDao;
import com.neusoft.dxz.module.system.demo.dao.RoleRedisDao;
import com.neusoft.dxz.module.system.demo.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resource Manager
 *
 * @author andy.jiao@msn.com
 */
@Component
public class ResourceManager {

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RoleResourceDao roleResourceDao;

    @Autowired
    private ResourceRedisDao resourceRedisDao;

    @Autowired
    private RoleRedisDao roleRedisDao;

    @Transactional
    public void add(Resource resource) {
        // 插入权限数据
        resourceDao.insert(resource);

        // 删除所有权限相关的缓存数据
        clearCache();
    }

    @Transactional
    public void update(Resource resource) {
        // 插入权限数据
        resourceDao.update(resource);

        // 删除所有权限相关的缓存数据
        clearCache();
    }

    @Transactional
    public void delete(Resource resource) {
        // 删除权限数据
        resourceDao.delete(resource);

        // 删除所有权限相关的缓存数据
        clearCache();
    }

    @Transactional
    public void sort(Resource res1, Resource res2) {
        resourceDao.update(res1);
        resourceDao.update(res2);

        // 删除所有权限相关的缓存数据
        this.resourceRedisDao.clear();
    }

    private void clearCache() {
        // 删除所有权限相关的缓存数据
        this.resourceRedisDao.clear();
        this.roleRedisDao.clear();
    }
}
