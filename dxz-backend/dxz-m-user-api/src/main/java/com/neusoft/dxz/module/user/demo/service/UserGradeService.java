package com.neusoft.dxz.module.user.demo.service;

import com.neusoft.dxz.module.user.demo.enums.UserGradeStatus;
import com.neusoft.dxz.module.user.demo.model.UserGrade;
import com.neusoft.features.common.model.Response;

import java.util.List;

/**
 * User Grade Service
 *
 * @author andy.jiao@msn.com
 */
public interface UserGradeService {

    /**
     * 获取全部用户等级信息。
     *
     * 对应组件：会员等级设定[console/member/level_config]
     *
     * @return 用户等级信息列表
     */
    Response<List<UserGrade>> all();

    /**
     * 创建用户等级。
     *
     * @param userGrade 用户等级信息
     * @return 成功/失败
     */
    Response<Boolean> create(UserGrade userGrade);

    /**
     * 更新用户等级。
     *
     * @param userGrade 用户等级信息
     * @return 成功/失败
     */
    Response<Boolean> update(UserGrade userGrade);

    /**
     * 删除用户等级。
     *
     * @param id 用户等级id
     * @return 成功/失败
     */
    Response<Boolean> delete(Long id);

    /**
     * 修改用户等级状态。
     *
     * @param id 用户等级id
     * @param status 用户等级状态
     * @return 成功/失败
     */
    Response<Boolean> changeStatus(Long id, UserGradeStatus status);
}



