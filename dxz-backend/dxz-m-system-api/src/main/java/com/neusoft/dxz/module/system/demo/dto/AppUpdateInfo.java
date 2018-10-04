package com.neusoft.dxz.module.system.demo.dto;

import lombok.Data;

import java.util.Map;

/**
 * App升级信息Dto
 *
 * @author andy.jiao@msn.com
 */
@Data
@Deprecated
public class AppUpdateInfo {

    /**
     * app版本信息
     */
    private AppVersionInfo app;

    /**
     * 基础数据更新信息。
     *
     * key-value形式，包含以下数据：
     *
     * newVersion：基础数据版本
     * 基础数据key：基础数据Model数组
     */
    private Map<String, Object> data;
}
