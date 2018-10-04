package com.neusoft.dxz.module.system.demo.manager;

import com.neusoft.dxz.module.system.demo.dao.AddressDao;
import com.neusoft.dxz.module.system.demo.dao.AddressRedisDao;
import com.neusoft.dxz.module.system.demo.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Address Manager
 *
 * @author andy.jiao@msn.com
 */
@Component
public class AddressManager {

    @Autowired
    private AddressRedisDao addressRedisDao;

    @Autowired
    private AddressDao addressDao;

    @Transactional
    public void add(Address address) {
        addressDao.insert(address);

        // 删除地区数据的所有缓存
        addressRedisDao.clear();
    }

    @Transactional
    public void update(Address address) {
        int count = this.addressDao.update(address);
        checkState(count == 1, "system.address.record.not.exist"); // 该地区不存在

        // 删除地区数据的所有缓存
        this.addressRedisDao.clear();
    }

    @Transactional
    public void delete(Long id) {
        int count = this.addressDao.deleteById(id);
        checkState(count == 1, "system.address.record.not.exist"); // 该地区不存在

        // 删除地区数据的所有缓存
        this.addressRedisDao.clear();
    }

    @Transactional
    public void sort(Address one, Address two) {
        one.setSort(two.getSort());
        this.addressDao.updateSort(one);

        two.setSort(one.getSort());
        this.addressDao.updateSort(two);

        // 删除地区数据的所有缓存
        this.addressRedisDao.clear();
    }
}
