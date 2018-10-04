package com.neusoft.dxz.module.user.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

/**
 * 用户关注信息
 *
 * @author andy.jiao@msn.com
 */
@Data
public class Follow extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 关注代码
     */
    private Long code;
    /**
     * 关注类型(1:参展企业;2:承办商;3:广告商;4:展会;5:商品)
     */
    private Integer type;
}
