package com.neusoft.dxz.module.message.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
public class PushVariant {

    public static String UMENG_IOS_APP_KEY;

    public static String UMENG_IOS_APP_SECRET;

    public static String UMENG_ANDROID_APP_KEY;

    public static String UMENG_ANDROID_APP_SECRET;

    private static final Properties props = new Properties();

    @Value("#{configProperties['umeng.ios.appkey']}")
    public void setUmengIosAppKey(String umengIosAppKey) {
        PushVariant.UMENG_IOS_APP_KEY = umengIosAppKey;
    }

    @Value("#{configProperties['umeng.ios.appsecret']}")
    public void setUmengIosAppSecret(String umengIosAppSecret) {
        PushVariant.UMENG_IOS_APP_SECRET = umengIosAppSecret;
    }

    @Value("#{configProperties['umeng.android.appkey']}")
    public void setUmengAndroidAppKey(String umengAndroidAppKey) {
        PushVariant.UMENG_ANDROID_APP_KEY = umengAndroidAppKey;
    }

    @Value("#{configProperties['umeng.android.appsecret']}")
    public void setUmengAndroidAppSecret(String umengAndroidAppSecret) {
        PushVariant.UMENG_ANDROID_APP_SECRET = umengAndroidAppSecret;
    }
}
