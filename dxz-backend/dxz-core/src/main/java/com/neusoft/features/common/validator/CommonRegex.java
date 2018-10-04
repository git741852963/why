package com.neusoft.features.common.validator;

/**
 * Common Regex Expression
 *
 * @author andy.jiao@msn.com
 */
public class CommonRegex {

    /** 地区名:只含有汉字、数字、字母、下划线，不能以下划线开头和结尾 */
    public static final String SYSTEM_ADDRESS_NAME = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";

    /** 用户名(字母开头,4-20位数字、字母、中划线、下划线) */
    public static final String USER_NAME = "^[a-zA-Z][-_0-9a-zA-Z]{3,19}$";

    /** 用户电话 2018.1 */
    public static final String USER_PHONE = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    /** 用户密码(6-20位数字、字母、符号) */
    public static final String USER_PASSWORD = "^([A-Z]|[a-z]|[0-9]|[`\\-=\\[\\];,./~!@#$%^&\\*()_+}{:?]){6,20}$";

    /** 用户密码(6-20位数字、字母、符号，可以为空(更新时check用)) */
    public static final String USER_PASSWORD_NULLABLE = "^$|^([A-Z]|[a-z]|[0-9]|[`\\-=\\[\\];,./~!@#$%^&\\*()_+}{:?]){6,20}$";

    /** 用户昵称(4-20位数字、字母、汉字、中划线、下划线) */
    public static final String USER_NICK = "^[\\u4e00-\\u9fa50-9a-zA-Z\\-_\\.]{4,20}$";
}
