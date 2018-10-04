package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Address;
import com.neusoft.features.common.dao.CommonDao;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Address Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class AddressDao extends CommonDao {

    public int insert(Address base) {
        return getSqlSession().insert("system.address.insert", base);
    }

    public int update(Address base) {
        return getSqlSession().update("system.address.update", base);
    }

    public Address findById(Long id) {
        return getSqlSession().selectOne("system.address.findById", id);
    }

    public Boolean isExist(Long pid, String name) {
        Map param = ImmutableMap.of("parentId", pid, "name", name);
        Integer count = getSqlSession().selectOne("system.address.isExist", param);
        return count > 0;
    }

    public List<Address> findByParentId(Long pid) {
        return getSqlSession().selectList("system.address.findByParentId", pid);
    }

    public int deleteById(Long id) {
        return getSqlSession().update("system.address.deleteById", id);
    }

    public List<Map<String, Object>> selectAddressById(Map<String, Object> para) {
        return getSqlSession().selectList("system.address.selectAddressById", para);
    }

    public int updateSort(Address base) {
        return getSqlSession().update("system.address.updateSort", base);
    }
}
