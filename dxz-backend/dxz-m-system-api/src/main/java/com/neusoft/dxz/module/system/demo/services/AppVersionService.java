package com.neusoft.dxz.module.system.demo.services;

import com.neusoft.dxz.module.system.demo.model.AppVersion;
import com.neusoft.features.common.model.Response;

/**
 * App版本管理接口。
 *
 * @author andy.jiao@msn.com
 */
public interface AppVersionService {

    Response<AppVersion> checkUpdate();
}
