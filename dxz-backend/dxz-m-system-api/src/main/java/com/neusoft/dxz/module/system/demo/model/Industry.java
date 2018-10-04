package com.neusoft.dxz.module.system.demo.model;


import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

/**
 * 行业信息
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Industry extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 行业名称
     */
    private String name;

    /**
     * 基础数据版本
     */
    private Integer ver;
}