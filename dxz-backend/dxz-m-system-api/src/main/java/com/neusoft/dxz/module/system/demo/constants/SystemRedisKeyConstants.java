package com.neusoft.dxz.module.system.demo.constants;

/**
 * System Redis Key Constant
 *
 * @author andy.jiao@msn.com
 */
public class SystemRedisKeyConstants {

    // 地区管理
    /**
     * 所有地区的缓存集合
     */
    public static final String ADDRESS_CACHE_COLLECTION = "address:cache:collections";

    /**
     * 取得地区的子地区
     */
    public static final String GET_ADDRESS_CHILDREN = "address:children:parentIs:";

    /**
     * 取得所有的一级地区
     */
    public static final String GET_ALL_ONE_LEVEL_ADDRESS = "address:allOneLevelAddress";

    /**
     * 取得地区的详细数据（省市县数据）
     */
    public static final String GET_DETAIL_ADDRESS = "address:detail:";

    /**
     * 取得单个地区的数据
     */
    public static final String GET_ADDRESSS_INFO = "address:info:";

    // 资源管理
    /**
     * 所有权限的缓存集合
     */
    public static final String AUTHORITY_CACHE_COLLECTION = "resource:cache:collections";
    /**
     * 根据角色和类型取得权限数据
     */
    public static final String GET_ROLE_AUTHORITY_INFO = "resource:role:info:";
    /**
     * 根据级别和类型取得权限数据
     */
    public static final String GET_CATEGORY_AUTHORITY_INFO = "resource:category:info:";

    // 角色管理
    /**
     * 所有角色的缓存集合
     */
    public static String ROLE_CACHE_COLLECTION = "role:cache:collections";
    /**
     * 取得角色详细信息(包含权限信息)
     */
    public static String GET_ROLE_DETAIL_INFO = "role:detail:info:";
    /**
     * 取得默认的角色(商家、平台商家、会员)
     */
    public static String GET_DEFAULT_ROLE_INFO = "role:default:info:";
}
