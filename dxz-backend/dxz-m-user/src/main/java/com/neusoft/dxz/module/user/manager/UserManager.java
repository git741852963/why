package com.neusoft.dxz.module.user.manager;

import com.neusoft.dxz.module.user.dao.UserDao;
import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.dxz.module.user.demo.service.UserRoleService;
import com.neusoft.features.common.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkState;

/**
 * User Manager
 *
 * @author andy.jiao@msn.com
 */
@Component
public class UserManager {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleService userRoleService;

    @Transactional
    public void consoleCreate(User user) {
        //TODO:这里有问题，三个操作，事务没有生效（第三个操作是干嘛来着？没实现）
        // 创建用户
        userDao.consoleCreate(user);

        // 插入用户权限
        Response<Boolean> result = userRoleService.bulkInsertUserRoles(user.getId(), user.getRoles());
        checkState(result.isSuccess(), result.getError());
    }

    @Transactional
    public void consoleUpdate(User user) {
        //TODO:这里有问题，三个操作，事务没有生效（第三个操作是干嘛来着？没实现）
        // 创建用户
        userDao.consoleEdit(user);

        // 插入用户权限
        Response<Boolean> result = userRoleService.bulkInsertUserRoles(user.getId(), user.getRoles());
        checkState(result.isSuccess(), result.getError());
    }
}
