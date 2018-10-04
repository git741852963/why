package com.neusoft.dxz.module.user.dao;

import com.neusoft.dxz.module.user.demo.model.UserGrade;
import com.neusoft.features.common.dao.CommonDao;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User Grade Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class UserGradeDao extends CommonDao {

    /**
     * 获取全部用户等级信息。
     *
     * @return 用户等级列表
     */
    public List<UserGrade> all() {
        return getSqlSession().selectList("user.grade.all");
    }

    /**
     * 根据等级名称查找用户等级信息。
     *
     * @param name 用户等级名称
     * @return 用户等级信息
     */
    public UserGrade findByName(String name) {
        return getSqlSession().selectOne("user.grade.findByName", name);
    }

    /**
     * 创建用户等级。
     *
     * @param userGrade 用户等级信息
     * @return 创建记录数
     */
    public int create(UserGrade userGrade) {
        return getSqlSession().insert("user.grade.create", userGrade);
    }

    /**
     * 更新用户等级。
     *
     * @param userGrade 用户等级信息
     * @return 更新记录数
     */
    public int update(UserGrade userGrade) {
        return getSqlSession().update("user.grade.update", userGrade);
    }

    /**
     * 根据名称、id，判断用户等级是否存在。
     *
     * @param id 用户等级ID
     * @param name 等级名称
     * @return 存在/不存在
     */
    public boolean isExist(Long id, String name) {
        Map param = ImmutableMap.of("id", id, "name", name);
        return (long)getSqlSession().selectOne("user.grade.isExist", param) > 0;
    }

    /**
     * 删除用户等级。
     *
     * @param id 用户等级ID
     * @return 删除记录数
     */
    public int delete(Long id) {
        return getSqlSession().update("user.grade.delete", id);
    }

    /**
     * 修改用户等级状态。
     *
     * @param id 用户等级ID
     * @param status 用户等级状态
     * @return 更新记录数
     */
    public int changeStatus(Long id, Integer status) {
        Map param = ImmutableMap.of("id", id, "status", status);
        return getSqlSession().update("user.grade.changeStatus", param);
    }
}
