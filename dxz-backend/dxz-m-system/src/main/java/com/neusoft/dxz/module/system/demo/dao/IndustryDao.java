package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Industry;
import com.neusoft.features.common.dao.CommonDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 行业相关DAO。
 *
 * @author guozhangjie
 */
@Repository
public class IndustryDao extends CommonDao {

    /**
     * 查找所有行业。
     *
     * @return 行业列表
     */
    public List<Industry> all() {
        return getSqlSession().selectList("system.industry.all");
    }

    /**
     * 根据ID查找行业信息。
     *
     * @return 行业信息
     */
    public Industry findById(Long industryId) {
        return getSqlSession().selectOne("system.industry.findById", industryId);
    }
}
