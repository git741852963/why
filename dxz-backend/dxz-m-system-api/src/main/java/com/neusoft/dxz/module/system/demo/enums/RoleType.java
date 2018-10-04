package com.neusoft.dxz.module.system.demo.enums;

import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 角色类型。
 *
 * @author andy.jiao@msn.com
 */
public enum RoleType implements ValuedEnum<Integer> {

    /** 管理员 */
    MANAGER(999, "管理员", ResourceType.CONSOLE),

    /** 前端用户 */
    USER(1, "现场运营");

    private final int value;

    private final String description;

    private final ResourceType resourceType;

    private RoleType(int value, String description) {
        this(value, description, null);
    }

    private RoleType(int value, String description, ResourceType resourceType) {
        this.value = value;
        this.description = description;
        this.resourceType = resourceType;
    }

    public Integer value() {
        return this.value;
    }

    public ResourceType resource() {
        return this.resourceType;
    }

    public static RoleType from(String name) {
        RoleType[] types = values();
        int len$ = types.length;

        for(int i = 0; i < len$; ++i) {
            RoleType type = types[i];
            if(Objects.equal(type.name(), name.toUpperCase())) {
                return type;
            }
        }

        return null;
    }

    public static RoleType from(Integer number) {
        if(number == null) {
            return null;
        } else {
            RoleType[] types = values();
            int len$ = types.length;

            for(int i = 0; i < len$; ++i) {
                RoleType type = types[i];
                if(Objects.equal(Integer.valueOf(type.value), number)) {
                    return type;
                }
            }
        }

        return null;
    }

    public static List<NameValuePair> toList() {
        RoleType[] types = values();
        int len$ = types.length;

        List<NameValuePair> list = Lists.newArrayListWithCapacity(len$);

        for(int i = 0; i < len$; ++i) {
            RoleType type = types[i];
            list.add(new NameValuePair(type.toString(), type.value()));
        }

        return list;
    }


    public static List<NameValuePair> allToListExcept(RoleType roleType) {
        RoleType[] types = values();
        int len$ = types.length;

        List<NameValuePair> list = Lists.newArrayListWithCapacity(len$);

        for(int i = 0; i < len$; ++i) {
            RoleType type = types[i];
            if (roleType != type) {
                list.add(new NameValuePair(type.toString(), type.value()));
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return description;
    }
}
