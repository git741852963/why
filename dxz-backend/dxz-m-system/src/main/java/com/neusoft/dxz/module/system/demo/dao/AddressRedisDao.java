package com.neusoft.dxz.module.system.demo.dao;

import com.neusoft.dxz.module.system.demo.model.Address;
import com.neusoft.dxz.module.system.demo.constants.SystemRedisKeyConstants;
import com.neusoft.features.redis.dao.RedisClusterDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * Address Redis Dao
 *
 * @author andy.jiao@msn.com
 */
@Repository
public class AddressRedisDao extends RedisClusterDao<Address> {

    /**
     * 取得所有的子地区
     *
     * @param id 地区ID
     * @return 所有的子地区
     */
    public List<Address> findChildrenById(Long id) {
        return fromHashFieldToList(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ADDRESS_CHILDREN + id);
    }

    /**
     * 根据父地区id保存所有的子地区
     *
     * @param id        父地区ID
     * @param addresses 所有的子地区
     */
    public void setChildrenById(Long id, List<Address> addresses) {
        toHashField(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ADDRESS_CHILDREN + id, addresses);
    }

    /**
     * 取得所有的一级地区
     *
     * @return 一级地区
     */
    public HashMap<String, Object> findAllOneLevel() {
        return fromHashFieldToMap(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ALL_ONE_LEVEL_ADDRESS);
    }

    /**
     * 保存所有的一级地区
     *
     * @param addresses 一级地区
     */
    public void setTopLevelAddresses(HashMap addresses) {
        toHashField(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ALL_ONE_LEVEL_ADDRESS, addresses);
    }

    /**
     * 查找地区的省市县数据
     *
     * @param provinceId 省ID
     * @param cityId     市ID
     * @param regionId   区县ID
     * @return 省市县数据
     */
    public HashMap<String, Object> findAddressById(Long provinceId, Long cityId, Long regionId) {
        String key = String.valueOf(provinceId) + ":" + String.valueOf(cityId) + ":" + String.valueOf(regionId);
        return fromHashFieldToMap(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_DETAIL_ADDRESS + key);
    }

    /**
     * 保存地区的省市县数据
     *
     * @param address 省市县数据
     */
    public void setAddressById(Long provinceId, Long cityId, Long regionId, HashMap address) {
        String key = String.valueOf(provinceId) + ":" + String.valueOf(cityId) + ":" + String.valueOf(regionId);
        toHashField(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_DETAIL_ADDRESS + key, address);
    }

    /**
     * 取得地区数据
     *
     * @param addressId 地区ID
     * @return 地区数据
     */
    public Address findAddressById(Long addressId) {
        return fromHashField(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ADDRESSS_INFO + addressId);
    }

    /**
     * 保存地区数据
     *
     * @param address 地区数据
     */
    public void setAddressById(Long addressId, Address address) {
        toHashField(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION, SystemRedisKeyConstants.GET_ADDRESSS_INFO + addressId, address);
    }

    /**
     * 删除所有地区相关的缓存数据
     */
    public void clear() {
        this.jedisAdapter.del(SystemRedisKeyConstants.ADDRESS_CACHE_COLLECTION);
    }
}
