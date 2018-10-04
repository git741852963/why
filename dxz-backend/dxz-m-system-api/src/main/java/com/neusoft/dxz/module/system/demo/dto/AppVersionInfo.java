package com.neusoft.dxz.module.system.demo.dto;

/**
 * @author andy.jiao@msn.com
 */
@Deprecated
public class AppVersionInfo {

    // 是否需要升级
    private boolean needUpdate;

    // 是否强制升级
    private boolean force;

    // 新版本号
    private String newVersion;

    // ios app上架号
    private String appId;

    // android app 下载地址
    private String download;
}
