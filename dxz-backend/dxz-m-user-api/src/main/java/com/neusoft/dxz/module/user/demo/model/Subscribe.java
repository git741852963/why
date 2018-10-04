package com.neusoft.dxz.module.user.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

/**
 * 消息订
 */
@Data
public class Subscribe extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 消息类型(1:企业消息 2:展会消息 3:广告消息 4:洽谈消息)
     */
    private Integer type;
}
