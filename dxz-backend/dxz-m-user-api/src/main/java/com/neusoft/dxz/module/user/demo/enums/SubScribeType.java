package com.neusoft.dxz.module.user.demo.enums;

import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;

/**
 * Created by Andy on 2016/7/12.
 */
public enum SubScribeType implements ValuedEnum<Integer> {

    /** 企业消息 */
    COMPANY(1, "COMPANY"),
    /** 展会消息 */
    EXHIBITION(2, "EXHIBITION"),
    /** 广告消息 */
    AD(4, "AD"),
    /** 洽谈消息 */
    CONVERSATION(8, "CONVERSATION");

    private final int value;

    private final String description;

    private SubScribeType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer value() {
        return this.value;
    }

    public static SubScribeType from(String name) {
        SubScribeType[] types = values();
        int len$ = types.length;

        for(int i = 0; i < len$; ++i) {
            SubScribeType type = types[i];
            if(Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static SubScribeType from(Integer number) {
        if(number == null) {
            return null;
        } else {
            SubScribeType[] types = values();
            int len$ = types.length;

            for(int i = 0; i < len$; ++i) {
                SubScribeType type = types[i];
                if(Objects.equal(Integer.valueOf(type.value), number)) {
                    return type;
                }
            }
        }

        return null;
    }

    public boolean isPartOf(Integer number) {
        return (this.value & number) == this.value;
    }

    @Override
    public String toString() {
        return description;
    }
}
