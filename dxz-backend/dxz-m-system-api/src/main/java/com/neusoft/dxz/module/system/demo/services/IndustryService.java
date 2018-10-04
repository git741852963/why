package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.model.Industry;
import com.neusoft.features.common.model.Response;

import java.util.List;

/**
 * 行业管理接口。
 * <p/>
 * 摘要：提供行业信息CRUD功能。
 * <p/>
 *
 * @author guozhangjie
 */
public interface IndustryService {

    /**
     * 查找所有行业信息。
     *
     * @return 行业列表
     */
    Response<List<Industry>> all();

    /**
     * 根据id查找行业。
     *
     * 未找到返回错误。
     *
     * @return 行业信息
     */
    Response<Industry> findById(Long industryId);
}

