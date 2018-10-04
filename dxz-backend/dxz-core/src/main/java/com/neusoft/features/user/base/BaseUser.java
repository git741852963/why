package com.neusoft.features.user.base;

import java.util.List;

public interface BaseUser {
    /** 取得用户ID */
    Long getId();

    /** 取得用户名 */
    String getName();

    /** 取得用户昵称 */
    String getNick();

    /** 获取电话 */
    String getPhone();

    /** 取得用户真实姓名 */
    String getRealName();

    /** 获取用户角色类型 */
    Integer getType();

    /** 获取用户角色列表 */
    List<Long> getRoles();

    /** 获取父用户ID */
    Long getParentId();

    /** 是否为管理员 */
    Boolean isManager();

    /** 是否为超级管理员 */
    Boolean isSuperMan();
}