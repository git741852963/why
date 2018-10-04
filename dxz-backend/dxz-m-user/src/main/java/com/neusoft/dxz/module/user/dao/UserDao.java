package com.neusoft.dxz.module.user.dao;

import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.features.common.dao.CommonDao;
import com.neusoft.features.dto.CommonDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * User Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class UserDao extends CommonDao {


    public int frozen(List<String> ids) {
        return getSqlSession().update("user.user.frozen", ids);
    }

    public int unfrozen(List<String> ids) {
        return getSqlSession().update("user.user.unfrozen", ids);
    }

    public long count(CommonDto param) {
        return getSqlSession().selectOne("user.user.count", param);
    }

    public List<User> listUser(CommonDto param) {
        return getSqlSession().selectList("user.user.listUser", param);
    }

    public int consoleCreate(User user) {
        return getSqlSession().insert("user.user.consoleCreate", user);
    }

    public int consoleEdit(User user) {
        return getSqlSession().update("user.user.consoleEdit", user);
    }

    public User findById(Long id) {
        return getSqlSession().selectOne("user.user.findById", id);
    }

    public User findByMobile(String mobile) {
        return getSqlSession().selectOne("user.user.findByMobile", mobile);
    }

    public int delete(Long id) {
        return getSqlSession().update("user.user.delete", id);
    }

    public void updatePassword(User User) {
        getSqlSession().update("user.user.updatePassword", User);
    }

    public User findByName(String userName) {
        return getSqlSession().selectOne("user.user.findByName", userName);
    }

    ////////////////////////////

    public int insert(User user) {
        return getSqlSession().insert("user.user.insert", user);
    }

    public int update(User user) {
        return getSqlSession().update("user.user.update", user);
    }


    public int update(CommonDto<User> user) {
        return getSqlSession().update("user.user.updateD", user);
    }

    public int updateGrowth(User user) {
        return getSqlSession().update("user.user.updateGrowth", user);
    }


    public User selectOne(User user) {
        return getSqlSession().selectOne("user.user.selectOne", user);
    }


    public List<User> selectList(CommonDto<User> user) {
        return getSqlSession().selectList("user.user.selectList", user);
    }


    public User selectOne(CommonDto<User> user) {
        return getSqlSession().selectOne("user.user.selectOneD", user);
    }

    public User findUserByEmail(String email) {
        return getSqlSession().selectOne("user.user.findByEmail", email);
    }

    public Long countByCondition(CommonDto<User> user) {
        return getSqlSession().selectOne("user.user.countByCondition", user);
    }

    public List<User> find(CommonDto<User> user) {
        return getSqlSession().selectList("user.user.find", user);
    }

    public List<User> findByUserIds(List<String> userIds) {
        return getSqlSession().selectList("user.user.findByUserIds", userIds);
    }

    public List<User> findByUserNames(List<String> userNames) {
        return getSqlSession().selectList("user.user.findByUserNames", userNames);
    }

    public List<User> findListByName(String userName) {
        return getSqlSession().selectList("user.user.findListByName", userName);
    }

    public List<User> selectUsers(CommonDto<User> userDto) {
        return getSqlSession().selectList("user.user.selectUsers", userDto);
    }

    public List<User> selectUsersLimit(CommonDto<User> userDto) {
        return getSqlSession().selectList("user.user.selectUsersLimit", userDto);
    }

    public Long selectUsersCount(CommonDto<User> userDto) {
        return getSqlSession().selectOne("user.user.selectUsersCount", userDto);
    }

    public List<HashMap<String, Object>> quantification() {
        return getSqlSession().selectList("user.user.quantification");
    }

    public void updateMembershipGrade(User user) {
        getSqlSession().update("user.user.updateMembershipGrade", user);
    }

    public void batchUpdateMembershipGrade(List<User> list) {
        getSqlSession().update("user.user.batchUpdateMembershipGrade", list);
    }

    public List<User> findUsersByGrade(List<String> gradeIds) {
        return getSqlSession().selectList("user.user.selectUsersByGrade", gradeIds);
    }

    public long appCount(CommonDto param) {
        return getSqlSession().selectOne("user.user.appCount", param);
    }

    public List<User> appListUser(CommonDto param) {
        return getSqlSession().selectList("user.user.appListUser", param);
    }

}
