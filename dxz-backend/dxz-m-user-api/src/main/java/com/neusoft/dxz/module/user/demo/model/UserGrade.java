package com.neusoft.dxz.module.user.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

/**
 * 用户等级基础信息
 *
 * @author andy.jiao@msn.com
 */
@Data
public class UserGrade extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 用户等级名称
     */
    private String name;
    /**
     * 等级图标
     */
    private String icon;
    /**
     * 最小值
     */
    private Long min;
    /**
     * 最大值
     */
    private Long max;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 状态
     */
    private String status;
    /**
     * 描述
     */
    private String description;
}
    

 