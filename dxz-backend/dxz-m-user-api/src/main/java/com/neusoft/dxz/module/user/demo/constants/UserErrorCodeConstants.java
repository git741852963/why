package com.neusoft.dxz.module.user.demo.constants;

/**
 * 用户模块错误码。
 * 、
 * @author andy.jiao@msn.com
 */
public class UserErrorCodeConstants {

    /** 用户未登录 */
    public static final String USER_NOT_LOGIN = "E10000";

    /** 用户无权限 */
    public static final String USER_UNAUTHORIZED = "E10001";

    /** 用户登录失败 */
    public static final String USER_LOGIN_FAILED = "E10002";

    /** 用户未找到 */
    public static final String USER_NOT_FOUND = "E10003";

    /** 需要验证码(登录超过2次) */
    public static final String NEED_IDENTIFY_CODE = "E10004";
}
