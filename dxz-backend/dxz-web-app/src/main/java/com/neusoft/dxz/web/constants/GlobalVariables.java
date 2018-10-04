package com.neusoft.dxz.web.constants;

import com.neusoft.features.common.SystemEnviroment;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 环境变量定义。
 * <p/>
 * 仅提供给Web以及框架包使用，各Dubbo服务不应该使用此接口。
 *
 * @author andy.jiao@msn.com
 */
public class GlobalVariables implements SystemEnviroment {

    private static final Logger log = LoggerFactory.getLogger(GlobalVariables.class);
    private final Properties props = new Properties();

    public GlobalVariables() {
        InputStream input = null;
        try {
            input = Resources.asByteSource(Resources.getResource("config/app.properties")).openStream();
            this.props.load(input);
            return;
        } catch (Exception e) {
            log.error("failed to load app.properties", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }

        log.info("read properties from global variables, running mode=" + getRunningMode());
    }

    @Override
    public String getRunningMode() {
        return props.getProperty("system.envrionment");
    }

    @Override
    public String getAssetsHome() {
        return props.getProperty("web.site.assetsHome");
    }

    @Override
    public String getSiteDomain() {
        return props.getProperty("web.site.domain");
    }

    @Override
    public String getBaseDomain() {
        return props.getProperty("web.base.domain");
    }

    @Override
    public String getSecLvlDomain() {
        return props.getProperty("web.second.level.domain");
    }

    @Override
    public String getFileServerDomain() {
        return props.getProperty("file.server.domain");
    }

    @Override
    public String getUploadFileRoot() {
        return props.getProperty("file.upload.root");
    }

    public String getProp(String key) {
        return props.getProperty(key);
    }

    public String getProp(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
