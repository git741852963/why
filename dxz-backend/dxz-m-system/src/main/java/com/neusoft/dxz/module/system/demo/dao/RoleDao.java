package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Role;
import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.dto.CommonDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Role Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class RoleDao extends CommonDao {

    public int insert(Role role) {
        return getSqlSession().insert("system.role.insert", role);
    }

    public int update(Role role) {
        return getSqlSession().update("system.role.update", role);
    }

    public int updateDefaultOthers(Role role) {
        return getSqlSession().update("system.role.updateDefaultOthers", role);
    }

    public int delete(Long id) {
        return getSqlSession().delete("system.role.delete", id);
    }

    public Role selectOne(CommonDto<Role> role) {
        return getSqlSession().selectOne("system.role.selectOne", role);
    }

    public List<Role> selectList(CommonDto<Role> role) {
        return getSqlSession().selectList("system.role.selectList", role);
    }

    public List<Role> findByIds(List<Long> roleIds) {
        return getSqlSession().selectList("system.role.findByIds", roleIds);
    }

    public Role findById(Long id) {
        return getSqlSession().selectOne("system.role.findById", id);
    }

    //TODO:卖家相关处理干掉
    /**
     * 查询角色名字（for 商家子账号管理）
     *
     * @param roleIds role ids
     * @return 角色名字列表
     */
    public List<String> selectRoleNames(List<String> roleIds) {
        return getSqlSession().selectList("system.role.selectRoleNames", roleIds);
    }

    /**
     * 查询商家相关角色（for 商家子账号管理）
     *
     * @return 角色列表
     */
    public List<Role> selectStoreRoles() {
        return getSqlSession().selectList("system.role.selectStoreRoles");
    }

}