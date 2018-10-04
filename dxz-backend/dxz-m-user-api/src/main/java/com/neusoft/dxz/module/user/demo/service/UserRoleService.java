package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.features.common.model.Response;

import java.util.List;

/**
 * User Role Service
 *
 * @author andy.jiao@msn.com
 */
public interface UserRoleService {
    /**
     * 根据用户ID查找用户角色ID列表
     *
     * @param id 用户ID
     * @return 角色ID列表
     */
    Response<List<Long>> findRoleIdsByUserId(Long id);

    /**
     * 批量插入用户橘色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 角色ID列表
     */
    Response<Boolean> bulkInsertUserRoles(Long userId, List<Long> roleIds);
}
