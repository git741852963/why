package com.neusoft.dxz.module.user.demo.enums;

import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;

/**
 * User Grade Status
 *
 * @author andy.jiao@msn.com
 */
public enum UserGradeStatus implements ValuedEnum<Integer> {

    /**
     * 启用
     */
    ENABLED(0, "启用"),

    /**
     * 停用
     */
    DISABLED(1, "停用");

    private final int value;

    private final String description;

    private UserGradeStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static UserGradeStatus from(String name) {
        UserGradeStatus[] types = values();
        int len$ = types.length;

        for (int i = 0; i < len$; ++i) {
            UserGradeStatus type = types[i];
            if (Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static UserGradeStatus from(Integer number) {
        if (number == null) {
            return null;
        } else {
            UserGradeStatus[] types = values();
            int len$ = types.length;

            for (int i = 0; i < len$; ++i) {
                UserGradeStatus type = types[i];
                if (Objects.equal(Integer.valueOf(type.value), number)) {
                    return type;
                }
            }
        }

        return null;
    }

    public Integer value() {
        return this.value;
    }

    @Override
    public String toString() {
        return description;
    }
}