package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Resource;
import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.dto.CommonDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资源Dao。
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class ResourceDao extends CommonDao {

    public int insert(Resource base) {
        return getSqlSession().insert("system.resource.insert", base);
    }

    public int update(Resource base) {
        return getSqlSession().update("system.resource.update", base);
    }

    public int delete(Resource base) {
        return getSqlSession().delete("system.resource.delete", base);
    }

    public Resource selectOne(CommonDto<Resource> base) {
        return getSqlSession().selectOne("system.resource.selectOne", base);
    }

    public List<Resource> selectList(CommonDto<Resource> base) {
        return getSqlSession().selectList("system.resource.selectList", base);
    }

    public Long max(CommonDto<Resource> base) {
        return getSqlSession().selectOne("system.resource.max", base);
    }

    public Long count(CommonDto<Resource> base) {
        return getSqlSession().selectOne("system.resource.count", base);
    }

    public List<Resource> all() {
        return getSqlSession().selectList("system.resource.all");
    }

    public List<Resource> findTopLevelResources(Integer category) {
        return getSqlSession().selectList("system.resource.findTopLevelResources", category);
    }

    public List<Resource> findResourcesByRoleId(List<Long> ids) {
        return getSqlSession().selectList("system.resource.findResourcesByRoleId", ids);
    }
}
