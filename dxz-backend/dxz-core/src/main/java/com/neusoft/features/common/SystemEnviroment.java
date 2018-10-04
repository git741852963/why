package com.neusoft.features.common;

/**
 * 系统环境接口。
 * <p/>
 * 仅提供给Web以及框架包使用，各Dubbo服务不应该使用此接口。
 *
 * @author andy.jiaomsn.com
 */
public interface SystemEnviroment {

    /**
     * dev / uat / pre / prod
     */
    String getRunningMode();

    /**
     * 前端资源目录
     */
    String getAssetsHome();

    /**
     * 站点域名
     */
    String getSiteDomain();

    /**
     * 一级域名
     */
    String getBaseDomain();

    /**
     * 二级域名
     */
    String getSecLvlDomain();

    /**
     * 文件服务器二级域名 (eg. file)
     */
    String getFileServerDomain();

    /**
     * 文件根目录 (eg. /var/local/webresource
     */
    String getUploadFileRoot();
}
