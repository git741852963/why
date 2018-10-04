package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.dao.AddressDao;
import com.neusoft.dxz.module.system.demo.dao.AddressRedisDao;
import com.neusoft.dxz.module.system.demo.manager.AddressManager;
import com.neusoft.dxz.module.system.demo.model.Address;
import com.neusoft.dxz.module.system.demo.validator.group.BaseValidatorGroup;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.common.service.BaseService;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.neusoft.features.user.base.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Address Service。
 *
 * @author andy.jiao@msn.com
 */
@Service
public class AddressServiceImpl extends BaseService implements AddressService {

    //system.address.id.already.exist=xxxx已经存在

    @Autowired
    private AddressRedisDao addressRedisDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private AddressManager addressManager;

    private final LoadingCache<Long, List<Address>> children;

    private final LoadingCache<Long, Address> self;

    private final List<Address> provinces;

    @Autowired
    public AddressServiceImpl(final AddressDao dao) {
        this.addressDao = dao;
        this.provinces = addressDao.findByParentId(1l);
        this.children = CacheBuilder.newBuilder().build(new CacheLoader<Long, List<Address>>() {
            @Override
            public List<Address> load(Long id) throws Exception {
                return addressDao.findByParentId(id);
            }
        });
        this.self = CacheBuilder.newBuilder().build(new CacheLoader<Long, Address>() {
            @Override
            public Address load(Long id) throws Exception {
                return addressDao.findById(id);
            }
        });
    }

    /**
     * 创建地址信息。
     * <p/>
     * 根据传入的地址数据创建地址信息，如果创建成功，返回地址信息。
     *
     * @param address 地址数据
     * @return 创建的地址信息
     */
    @Override
    public Response<Address> createAddress(Address address) {

        Response<Address> response = new Response<>();

        try {
            // 参数校验
            validate(address, BaseValidatorGroup.CREATE.class);

            // 根据地区编码查询DB
            Address addressDb = this.addressDao.findById(address.getId());
            if (addressDb != null) {
                response.setError("system.address.id.already.exist");
                // 地区已存在，返回
                return response;
            }

            // 根据父节点地区编码查询DB
            addressDb = this.addressDao.findById(address.getParentId());
            if (addressDb == null) {
                // 地区已存在，返回
                response.setError("system.address.parent.id.not.exist");
                return response;
            }

            // 判断父节点下同名地区是否存在
            boolean isExist = this.addressDao.isExist(address.getParentId(), address.getName());
            checkState(!isExist, "system.address.name.already.exist");

            // 保存地区信息
            address.setName(address.getName().trim());
            addressManager.add(address);
            response.setResult(address);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("failed to add new address, address={}, cause:{}", address.toJson(), e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to add new address, address={}, cause:{}", address.toJson(), Throwables.getStackTraceAsString(e));
            response.setError("system.address.create.fail");
        }

        return response;
    }

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
    @Override
    public Response<Boolean> deleteAddress(Address address) {

        Response<Boolean> response = new Response<>();

        try {
            // 地区或地区ID不能为空 // 该地区不存在
            validate(address, BaseValidatorGroup.DELETE.class);

            List<Address> children = this.addressDao.findByParentId(address.getId());
            if (children != null && children.size() > 0) {
                response.setError("system.address.subAddress.exist"); // 该地区存在子地区，不能删除
                return response;
            }

            // 删除地区
            addressManager.delete(address.getId());
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to delete address, address={}, cause:{}", address.toJson(), e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to delete address, address={}, cause:{}", address.toJson(),
                      Throwables.getStackTraceAsString(e));
            response.setError("system.address.delete.fail");
        }

        return response;
    }

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
    @Override
    public Response<Boolean> updateName(Address address) {

        Response<Boolean> response = new Response<>();

        try {
            // ID不能为空 // 地区名不能为空 // 地区名称不能用特殊字符
            validate(address, BaseValidatorGroup.UPDATE.class);

            //            if (address == null || Strings.isNullOrEmpty(address.getId())) {
            //                log.info("update address failed. id can not be null.");
            //                return this.produce(ResponseDescription.RESPONSE_FAILED_2, new Object[]{"更新地区名", ""});
            //            }
            //            if (address == null || Strings.isNullOrEmpty(address.getName().trim())) {
            //                log.info("update address failed. address name can not be null");
            //                return this.produce(ResponseDescription.RESPONSE_FAILED_2, new Object[]{"更新地区名", ""});
            //            }
            //            // hack 增加地区ID，地区名称的输入合法性校验
            //            Boolean check = VariableUtils.checkVariableCorrect(address.getName());
            //            if (!check) {
            //                return this.produce(ResponseDescription.RESPONSE_FAILED_2, new Object[]{"更新地区名", "地区名称不能用特殊字符"});
            //            }
            address.setName(address.getName().trim());
            addressManager.update(address);
        } catch (IllegalArgumentException e) {
            log.error("failed to update address, address={}, cause:{}", address.toJson(), e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to update address, address={}, cause:{}", address.toJson(),
                      Throwables.getStackTraceAsString(e));
            response.setError("system.address.update.fail");
        }

        return response;
    }

    /**
     * 更新地区显示顺序
     *
     * @param firstId  地区ID
     * @param secondId 地区ID
     * @return 执行结果
     */
    @Override
    public Response<Boolean> updateSort(Long firstId, Long secondId) {

        Response<Boolean> response = new Response<>();

        try {
            // 地区ID不能为空
            checkArgument(firstId != null && secondId != null, "system.address.param.id.null");

            Address addressOne = this.addressDao.findById(firstId);
            if (addressOne == null) {
                response.setError("system.address.record.not.exist");
                return response;
            }

            Address addressTwo = this.addressDao.findById(secondId);
            if (addressTwo == null) {
                response.setError("system.address.record.not.exist");
                return response;
            }

            // 交换顺序
            addressManager.sort(addressOne, addressTwo);
            response.setResult(true);
        } catch (IllegalArgumentException e) {
            log.error("failed to sort address, id 1={}, id 2={}, cause:{}", firstId, secondId, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to sort address, id 1={}, id 2={}, cause:{}", firstId, secondId,
                      Throwables.getStackTraceAsString(e));
            response.setError("system.address.sort.fail");
        }

        return response;
    }

    /**
     * 查找所有的子地区（从Redis中查询）
     *
     * @param id 地区ID
     * @return 所有的子地区
     */
    @Override
    public Response<List<Address>> findChildrenByIdCache(Long id) {
        Response<List<Address>> response = new Response<>();

        try {
            // 地区ID不能为空
            checkArgument(id != null, "system.address.param.id.null");

            List<Address> addresses = this.addressRedisDao.findChildrenById(id);
            if (addresses == null) {
                addresses = this.addressDao.findByParentId(id);
                if (addresses != null && addresses.size() > 0) {
                    this.addressRedisDao.setChildrenById(id, addresses);
                }
                addresses = this.addressRedisDao.findChildrenById(id);
            }
            response.setResult(addresses);
        } catch (IllegalArgumentException e) {
            log.error("failed to find sub address from cache, parent id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find sub address from cache, parent id={}, cause:{}", id,
                      Throwables.getStackTraceAsString(e));
            response.setError("system.address.query.fail");
        }

        return response;
    }

    /**
     * 查找所有一级地区（从Redis中查询）
     *
     * @return 一级地区
     */
    @Override
    public Response<HashMap<String, Object>> findAllOneLevelCache() {
        Response<HashMap<String, Object>> response = new Response<>();

        try {
            HashMap<String, Object> result = this.addressRedisDao.findAllOneLevel();

            if (result == null) {
                result = new HashMap<>();
                List<Address> addresses = this.addressDao.findByParentId(1L);
                result.put("data", addresses);
                result.put("addressLevel", 1L);
                result.put("parentId", 1L);
                if (result.get("data") != null && ((List<Address>) result.get("data")).size() > 0) {
                    this.addressRedisDao.setTopLevelAddresses(result);
                }
                result = this.addressRedisDao.findAllOneLevel();
            }
            response.setResult(result);
        } catch (Exception e) {
            log.error("failed to find top level addresses, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.address.query.top.level.fail");
        }

        return response;
    }

    /**
     * 查找地区的省市县数据（从Redis中查询）
     *
     * @param provinceId 省
     * @param cityId     市
     * @param regionId   县
     * @return 地区的省市县数据
     */
    @Override
    public Response<HashMap<String, Object>> selectAddressByIdCache(Long provinceId, Long cityId, Long regionId) {

        Response<HashMap<String, Object>> response = new Response<>();

        try {
            // 地区ID不能为空
            checkArgument(provinceId != null && cityId != null && regionId != null,
                          "system.address.param.area.id.null");

            HashMap<String, Object> result = this.addressRedisDao.findAddressById(provinceId, cityId, regionId);
            if (result == null) {
                Map<String, Object> param = new HashMap<>();
                param.put("provinceId", provinceId);
                param.put("cityId", cityId);
                param.put("regionId", regionId);
                List<Map<String, Object>> addresses = this.addressDao.selectAddressById(param);
                if (addresses == null || addresses.size() == 0) {
                    response.setError("system.address.record.not.exist");
                    return response;
                } else {
                    this.addressRedisDao.setAddressById(provinceId, cityId, regionId,
                                                        (HashMap<String, Object>) addresses.get(0));
                    result = this.addressRedisDao.findAddressById(provinceId, cityId, regionId);
                }
            }
            response.setResult(result);
        } catch (IllegalArgumentException e) {
            log.error("failed to find address, provinceId={}, cityId={}, regionId={}, cause:{}", provinceId, cityId,
                      regionId, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find address, provinceId={}, cityId={}, regionId={}, cause:{}", provinceId, cityId,
                      regionId, e.getMessage());
            response.setError("system.address.query.fail");
        }

        return response;
    }

    /**
     * 取得地区数据（从Redis中查询）
     *
     * @param addressId 地区ID
     * @return 地区数据
     */
    @Override
    public Response<Address> findByIdCache(Long addressId) {

        Response<Address> response = new Response<>();

        try {
            // 地区ID不能为空
            checkArgument(addressId != null, "system.address.param.id.null");

            Address result = this.addressRedisDao.findAddressById(addressId);
            if (result == null) {
                Address address = this.addressDao.findById(addressId);
                checkState(address != null, "system.address.record.not.exist");
                this.addressRedisDao.setAddressById(addressId, address);
                result = address;
            }
            response.setResult(result);
        } catch (IllegalArgumentException e) {
            log.error("failed to find address, addressId={}, cause:{}", addressId, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find address, addressId={}, cause:{}", addressId, e.getMessage());
            response.setError("system.address.query.fail");
        }

        return response;
    }

    @Override
    public Response<List<Address>> provinces() {
        Response<List<Address>> response = new Response<>();
        try {
            response.setResult(provinces);
            return response;
        } catch (Exception e) {
            log.error("failed to find provinces, cause:{}", Throwables.getStackTraceAsString(e));
            response.setError("system.address.find.provinces.fail");
        }
        return response;
    }

    @Override
    public Response<List<Address>> citiesOf(Long id) {
        Response<List<Address>> response = new Response<>();

        try {
            checkArgument(id != null, "system.address.param.pid.null");

            List<Address> addresses = children.getUnchecked(id);
            response.setResult(addresses);
        } catch (IllegalArgumentException e) {
            log.error("failed to find cities, province id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find cities, province id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("system.address.find.cities.fail");
        }

        return response;
    }

    @Override
    public Response<List<Address>> districtOf(Long id) {
        Response<List<Address>> response = new Response<>();

        try {
            checkArgument(id != null, "system.address.param.cid.null");

            List<Address> addresses = children.getUnchecked(id);
            response.setResult(addresses);
        } catch (IllegalArgumentException e) {
            log.error("failed to find districts, city id={}, cause:{}", id, e.getMessage());
            response.setError(e.getMessage());
        } catch (Exception e) {
            log.error("failed to find districts, city id={}, cause:{}", id, Throwables.getStackTraceAsString(e));
            response.setError("system.address.find.districts.fail");
        }

        return response;
    }
}
