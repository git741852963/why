package com.neusoft.dxz.module.system.demo.enums;

import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;

/**
 * 客户端类型枚举类。
 *
 * @author andy.jiao@msn.com
 */
public enum ClientType implements ValuedEnum<Integer> {

    /** ios: 0 */
    IOS(0, "ios"),

    /** android：1 */
    ANDROID(1, "android"),

    /** pc：2 */
    PC(2, "pc"),

    /** wap：3  */
    WAP(3, "wap");

    private final int value;
    private final String description;

    private ClientType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ClientType from(int value) throws Exception {
        for (ClientType platform : ClientType.values()) {
            if (Objects.equal(platform.value(), value)) {
                return platform;
            }
        }
        throw new Exception("enum value illegal");
    }

    public Integer value() {
        return this.value;
    }

    @Override
    public String toString() {
        return description;
    }
}
