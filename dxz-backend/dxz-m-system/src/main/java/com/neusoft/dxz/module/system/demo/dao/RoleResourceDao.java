package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.RoleResource;
import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.dto.CommonDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoleAuthorityDao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class RoleResourceDao extends CommonDao {

    public int insert(RoleResource param) {
        return getSqlSession().insert("system.roleResource.insert", param);
    }

    public int update(RoleResource param) {
        return getSqlSession().update("system.roleResource.update", param);
    }

    public int delete(CommonDto<RoleResource> param) {
        return getSqlSession().delete("system.roleResource.delete", param);
    }

    public List<RoleResource> findByCondition(CommonDto<RoleResource> param) {
        return getSqlSession().selectList("system.roleResource.findByCondition", param);
    }

    public List<NameValuePair> findAllResRoles() {
        return getSqlSession().selectList("system.roleResource.findAllResRoles");
    }
}
