package com.neusoft.features.common.validator;

import java.util.regex.Pattern;

/**
 * @author andy.jiao@msn.com
 */
public class RegExConstants {

    /** 中文 */
    public static final String CHINESE = "^[\\u4e00-\\u9fa5]+$";

    /** 字母开头，包含英文字母、中划线、下划线 */
    public static final String COMMON_ENGLISH_WORD = "^[a-zA-Z][\\-_0-9a-zA-Z]+$";

    /** 中文、英文、数字、中划线、下划线、各种括号 */
    public static final String CHINESE_ENGLISH_NUMBER = "^[\\-_0-9a-zA-Z\\u4e00-\\u9fa5\\s()（）【】\\[\\]]+$";

    /** 中文、英文、数字、中划线、下划线、空格、各种括号 */
    public static final String COMMON_STRING = "^[\\-_0-9a-zA-Z\\u4e00-\\u9fa5\\s()（）【】\\[\\]]+$";

    /** 身份证（15位） */
    public static final String ID_CARD_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";

    /** 身份证（18位） */
    public static final String ID_CARD_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

    /** 手机号 */
    public static final String MOBILE_NUMBER = "^((13[0-9])|(15[^4,\\D])|(17[0678])|(18[0-9])|(14[57]))\\d{8}$";

    /** IPV4 */
    public static final String IP_V4 = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    /** IPV6 */
    public static final String IP_V6 = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";

    /** 二级域名（本系统内二级域名最长20） */
    public static final String SUB_DOMAIN = "^[a-zA-Z0-9]{1,20}$";

    /** Page URL，除domain外的url */
    public static final String PAGE_URL = "(/{0,1}[a-zA-Z0-9\\-_]+)+";


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile(CHINESE_ENGLISH_NUMBER);

        System.out.println(pattern.matcher("你好\\").matches());
        System.out.println(pattern.matcher("你好（）").matches());
        System.out.println(pattern.matcher("你好[]").matches());
        System.out.println(pattern.matcher("你好【】!").matches());
        System.out.println(pattern.matcher("你 好()").matches());
    }
}
