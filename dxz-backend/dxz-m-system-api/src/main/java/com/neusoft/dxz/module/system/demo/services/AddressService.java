package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.model.Address;
import com.neusoft.features.common.model.Response;

import java.util.HashMap;
import java.util.List;

/**
 * 地址信息数据维护接口。
 * <p/>
 * 摘要：提供对地址信息数据维护及数据缓存操作支持、提供对地址数据Redis缓存查询等操作。
 *
 * @author andy.jiao@msn.com
 */
public interface AddressService {

    /**
     * 创建地址信息。
     * <p/>
     * 根据传入的地址数据创建地址信息，如果创建成功，返回地址信息，否则返回check应答消息。
     * <p/>
     *
     * @param address 地址数据
     * @return 创建的地址信息
     */
    Response<Address> createAddress(Address address);

    /**
     * 删除地址信息。
     * <p/>
     * 根据传入的地址数据删除地址信息，返回操作应答信息。
     * <p/>
     * 以下参数必须：<br>
     * Address.id<br>
     * Address.addressId<br>
     *
     * @param address 地址数据
     * @return 操作应答
     */
    Response<Boolean> deleteAddress(Address address);

    /**
     * 更新地址信息。
     * <p/>
     * 根据传入的地址数据更新地址信息，返回操作应答信息。
     * <p/>
     * 以下参数必须：<br>
     * Address.id<br>
     * Address.name<br>
     *
     * @param address 地址数据
     * @return 操作应答
     */
    Response<Boolean> updateName(Address address);

    /**
     * 更新地址排序。
     * <p/>
     * 根据传入的地址数据更新地址排序，返回操作应答信息。
     * <p/>
     * 以下参数必须：<br>
     * Address.oneId<br>
     * Address.twoId<br>
     *
     * @param firstId 被更新对象ID
     * @param secondId 关联对象ID
     * @return 操作应答
     */
    Response<Boolean> updateSort(Long firstId, Long secondId);

    /**
     * 查找所有的子地区。
     * <p/>
     * 根据地址数据ID查找所有的子地区（从Redis中查询）。
     *
     * @param id 地区ID
     * @return 所有的子地区
     */
    Response<List<Address>> findChildrenByIdCache(Long id);

//    /**
//     * 查找所有的子地区。
//     * <p/>
//     * 根据地址数据ID查找所有的子地区（从DB中查询）。
//     *
//     * @param id 地区ID
//     * @return 所有的子地区
//     */
//    Response<List<Address>> findChildrenById(Long id);

    /**
     * 查找所有一级地区。
     * <p/>
     * 查找所有一级地区（从Redis中查询）。
     *
     * @return 一级地区
     */
    Response<HashMap<String, Object>> findAllOneLevelCache();

    /**
     * 查找地区的省市县数据
     * <p/>
     * 根据地址ID查找地区的省市县数据（从Redis中查询）。
     *
     * @param provinceId 省
     * @param cityId     市
     * @param regionId   县
     * @return 地区的省市县数据
     */
    Response<HashMap<String, Object>> selectAddressByIdCache(Long provinceId,
                                                             Long cityId,
                                                             Long regionId);

    /**
     * 取得地区数据
     * <p/>
     * 根据ID取得地区数据（从Redis中查询）。
     *
     * @param addressId 地区ID
     * @return 地区数据
     */
    Response<Address> findByIdCache(Long addressId);


    /**
     * 获取省份数据
     *
     * @return 省份数据数据
     */
    Response<List<Address>> provinces();

    /**
     * 获取省份数据
     *
     * @param id 省份ID
     * @return 省份数据数据
     */
    Response<List<Address>> citiesOf(Long id);

    /**
     * 获取地区数据
     *
     * @param id 城市ID
     * @return 省份数据数据
     */
    Response<List<Address>> districtOf(Long id);
}
