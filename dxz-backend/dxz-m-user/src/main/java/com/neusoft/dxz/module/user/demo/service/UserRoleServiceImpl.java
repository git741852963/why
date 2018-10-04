package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.dxz.module.user.dao.UserRoleDao;
import com.neusoft.dxz.module.user.manager.UserRoleManager;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User Role Service
 *
 * @author andy.jiao@msn.com
 */
@Service
public class UserRoleServiceImpl extends BaseService implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserRoleManager userRoleManager;

    @Override
    public Response<List<Long>> findRoleIdsByUserId(Long id) {

        Response<List<Long>> response = new Response<>();

        try {
            checkArgument(id != null, "user.role.param.id.null");
            List<Long> roleIds = userRoleDao.findRoleIdsByUserId(id);
            response.setResult(roleIds);
        } catch (IllegalArgumentException e) {
            log.error("failed to find role ids by user id, id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find role ids by user id, id={}, cause:{}", id, Throwables
                    .getStackTraceAsString(e));
            response.setError("user.role.find.roleIds.fail");
        }

        return response;
    }

    @Override
    public Response<Boolean> bulkInsertUserRoles(Long userId, List<Long> roleIds) {

        Response<Boolean> response = new Response<>();

        try {
            checkArgument(userId != null, "user.role.param.user.id.null");
            checkArgument(roleIds != null && roleIds.size() > 0, "user.role.param.id.null");

            userRoleManager.bulkInsertUserRoles(userId, roleIds);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to insert user roles, user id={}, role ids={}, cause:{}", userId, roleIds, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to insert user roles, user id={}, role ids={}, cause:{}", userId, roleIds, Throwables
                    .getStackTraceAsString(e));
            response.setError("user.role.insert.user.roles.fail");
        }

        return response;
    }
}
