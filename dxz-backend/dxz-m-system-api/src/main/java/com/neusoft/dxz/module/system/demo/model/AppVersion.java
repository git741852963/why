package com.neusoft.dxz.module.system.demo.model;

import com.neusoft.features.common.model.BaseModel;
import lombok.Data;

/**
 * APP版本信息<br>
 *
 * @author andy.jiao@msn.com
 */
@Data
public class AppVersion extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号
     */
    private String version;
    /**
     * 版本Code（安卓用）
     */
    private Integer code;
    /**
     * 下载链接
     */
    private String download;
    /**
     * 版本类型
     */
    private Integer type;
    /**
     * 版本说明
     */
    private String message;
    /**
     * 最小支持版本
     */
    private String minVersion;
    /**
     * 是否强制更新
     */
    private String isForce;
}
