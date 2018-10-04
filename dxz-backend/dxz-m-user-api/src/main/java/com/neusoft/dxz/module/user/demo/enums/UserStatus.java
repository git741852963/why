package com.neusoft.dxz.module.user.demo.enums;

import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

public enum UserStatus implements ValuedEnum<Integer> {

    /** 正常 */
    NORMAL(0, "正常"),
    /** 冻结 */
    FROZEN(1, "冻结");

    private final int value;

    private final String description;

    private UserStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer value() {
        return this.value;
    }

    public static UserStatus from(String name) {
        UserStatus[] types = values();
        int len$ = types.length;

        for(int i = 0; i < len$; ++i) {
            UserStatus type = types[i];
            if(Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static UserStatus from(Integer number) {
        if(number == null) {
            return null;
        } else {
            UserStatus[] types = values();
            int len$ = types.length;

            for(int i = 0; i < len$; ++i) {
                UserStatus type = types[i];
                if(Objects.equal(Integer.valueOf(type.value), number)) {
                    return type;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return description;
    }


    public static List<NameValuePair> toList() {
        UserStatus[] status = values();
        int len$ = status.length;

        List<NameValuePair> list = Lists.newArrayListWithCapacity(len$);

        for(int i = 0; i < len$; ++i) {
            UserStatus type = status[i];
            list.add(new NameValuePair(type.toString(), type.value()));
        }

        return list;
    }
}
