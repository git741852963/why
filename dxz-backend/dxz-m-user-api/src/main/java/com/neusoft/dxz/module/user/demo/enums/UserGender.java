package com.neusoft.dxz.module.user.demo.enums;

import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;

public enum UserGender implements ValuedEnum<Integer> {

    /** 男 */
    MALE(0, "男"),
    /** 女 */
    FEMALE(1, "女");

    private final int value;

    private final String description;

    private UserGender(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer value() {
        return this.value;
    }

    public static UserGender from(String name) {
        UserGender[] types = values();
        int len$ = types.length;

        for(int i = 0; i < len$; ++i) {
            UserGender type = types[i];
            if(Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static UserGender from(Integer number) {
        if(number == null) {
            return null;
        } else {
            UserGender[] types = values();
            int len$ = types.length;

            for(int i = 0; i < len$; ++i) {
                UserGender type = types[i];
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
}
