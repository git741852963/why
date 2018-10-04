package com.neusoft.features.enums;

import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 操作结果类型。
 *
 * @author andy.jiao@msn.com
 */
public enum OperationResultType implements ValuedEnum<Integer> {
    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    EXCEPTION(-1, "异常");

    private final int value;
    private final String description;

    OperationResultType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer value() {
        return this.value;
    }

    public static OperationResultType from(String name) {
        OperationResultType[] types = values();
        int len$ = types.length;

        for(int i = 0; i < len$; ++i) {
            OperationResultType type = types[i];
            if(Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static OperationResultType from(Integer number) {
        if(number == null) {
            return null;
        } else {
            OperationResultType[] types = values();
            int len$ = types.length;

            for(int i = 0; i < len$; ++i) {
                OperationResultType type = types[i];
                if(Objects.equal(Integer.valueOf(type.value), number)) {
                    return type;
                }
            }
        }

        return null;
    }

    public static List<NameValuePair> toList() {
        OperationResultType[] types = values();
        int len$ = types.length;

        List<NameValuePair> list = Lists.newArrayListWithCapacity(len$);

        for(int i = 0; i < len$; ++i) {
            OperationResultType type = types[i];
            list.add(new NameValuePair(type.toString(), type.value()));
        }

        return list;
    }

    @Override
    public String toString() {
        return description;
    }
}
