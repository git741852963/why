package com.neusoft.dxz.module.user.demo.model;

import com.neusoft.features.common.model.BaseModel;

/**
 * User Profile
 *
 * @author andy.jiao@msn.com
 */
public class UserProfile extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 总成长值
     */
    private String growth;
    /**
     * 总积分
     */
    private String point;
    /**
     * 登录次数
     */
    private String loginTimes;
    /**
     * 最后登录时间
     */
    private String lastLogin;
}
