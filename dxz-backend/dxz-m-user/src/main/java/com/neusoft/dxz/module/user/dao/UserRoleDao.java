package com.neusoft.dxz.module.user.dao;

import com.neusoft.features.common.dao.CommonDao;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User Role Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class UserRoleDao extends CommonDao {

    public List<Long> findRoleIdsByUserId(Long id) {
        return getSqlSession().selectList("user.role.findRoleIdsByUserId", id);
    }

    public List<Long> deleteOthers(Long userId, List<Long> ids) {
        Map param = ImmutableMap.of("userId", userId, "roleIds", ids);
        return getSqlSession().selectList("user.role.deleteOthers", param);
    }

    public List<Long> deleteAll(Long userId) {
        Map param = ImmutableMap.of("userId", userId);
        return getSqlSession().selectList("user.role.deleteOthers", param);
    }

    public List<Long> bulkInsertRoles(Long userId, List<Long> ids) {
        //TODO:使用了selectList...
        Map param = ImmutableMap.of("userId", userId, "roleIds", ids);
        return getSqlSession().selectList("user.role.bulkInsert", param);
    }
}
