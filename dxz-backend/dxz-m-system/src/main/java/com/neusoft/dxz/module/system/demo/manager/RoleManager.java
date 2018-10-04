package com.neusoft.dxz.module.system.demo.manager;

import com.neusoft.dxz.module.system.demo.dao.ResourceRedisDao;
import com.neusoft.dxz.module.system.demo.dao.RoleDao;
import com.neusoft.dxz.module.system.demo.dao.RoleRedisDao;
import com.neusoft.dxz.module.system.demo.dao.RoleResourceDao;
import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.dxz.module.system.demo.model.RoleResource;
import com.neusoft.features.dto.CommonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Role Manager
 *
 * @author andy.jiao@msn.com
 */
@Component
public class RoleManager {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleRedisDao roleRedisDao;

    @Autowired
    private RoleResourceDao roleResourceDao;

    @Autowired
    private ResourceRedisDao resourceReidsDao;

    @Transactional
    public void create(Role role) {
        // 保存角色信息
        roleDao.insert(role);

        // 保存角色关联权限信息
        for (RoleResource roleAuthorities : role.getRoleAuthorities()) {
            roleAuthorities.setRoleId(role.getId());
            roleResourceDao.insert(roleAuthorities);
        }

        // 删除所有角色相关的缓存数据
        this.roleRedisDao.clear();
    }

    @Transactional
    public void update(Role role) {
        // 先删除当前角色关联权限数据
        CommonDto<RoleResource> param = new CommonDto<>();
        param.addFlexibleData("roleId", role.getId());
        roleResourceDao.delete(param);

        // 重新保存角色关联权限信息
        for (RoleResource roleAuthorities : role.getRoleAuthorities()) {
            roleAuthorities.setRoleId(role.getId());
            roleResourceDao.insert(roleAuthorities);
        }

        // 更新角色信息
        roleDao.update(role);
        // 由于修改角色的时候可能角色对应的权限信息，因此需要删除所有权限相关的缓存数据
        this.resourceReidsDao.clear();
        // 删除所有角色相关的缓存数据
        this.roleRedisDao.clear();
    }

    @Transactional
    public void editDefault(Role role) {
        // 更新角色信息
        roleDao.update(role);
        if (role.isDefault()) {
            role.setDefault(false);
            roleDao.updateDefaultOthers(role);
        }
        // 删除所有角色相关的缓存数据
        this.roleRedisDao.clear();
    }

    @Transactional
    public void delete(Long id) {
        // 删除当前角色关联权限数据
        CommonDto<RoleResource> param = new CommonDto<>();
        param.addFlexibleData("roleId", id);
        roleResourceDao.delete(param);
        // 删除角色数据
        roleDao.delete(id);
        // 删除所有角色相关的缓存数据
        this.roleRedisDao.clear();
    }
}
