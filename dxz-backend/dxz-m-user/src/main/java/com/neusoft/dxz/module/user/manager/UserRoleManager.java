package com.neusoft.dxz.module.user.manager;

import com.neusoft.dxz.module.user.dao.UserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author andy.jiao@msn.com
 */
@Component
public class UserRoleManager {

    @Autowired
    private UserRoleDao userRoleDao;

    @Transactional
    public void bulkInsertUserRoles(Long userId, List<Long> roleIds) {
        // 删除在列表之外的角色ID
        userRoleDao.deleteAll(userId);

        // 批量添加新角色
        userRoleDao.bulkInsertRoles(userId, roleIds);
    }
}
