package com.neusoft.dxz.module.user.dao;

import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.features.common.dao.CommonDao;
import org.springframework.stereotype.Repository;

/**
 * User Profile Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class UserProfileDao extends CommonDao {

    public int insert(User user) {
        return getSqlSession().insert("user.profile.insert", user);
    }

}
