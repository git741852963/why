package com.neusoft.dxz.module.system.demo.enums;

import com.neusoft.features.common.dto.NameValuePair;
import com.neusoft.features.common.validator.ValuedEnum;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 资源类型
 *
 * @author andy.jiao@msn.com
 */
public enum ResourceType implements ValuedEnum<Integer> {

    /**
     * 菜单类型：0 - 管理后台
     */
    CONSOLE(0, "管理后台"),
    /**
     * 菜单类型：2 - 个人中心
     */
    USER(1, "个人中心");

    private final int value;

    private final String description;

    ResourceType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ResourceType from(int value) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (Objects.equal(resourceType.value(), value)) {
                return resourceType;
            }
        }
        return null;
    }

    public static ResourceType from(String name) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (name.equalsIgnoreCase(resourceType.name())) {
                return resourceType;
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

    public static List<NameValuePair> toList() {
        ResourceType[] types = values();
        int len$ = types.length;

        List<NameValuePair> list = Lists.newArrayListWithCapacity(len$);

        for(int i = 0; i < len$; ++i) {
            ResourceType type = types[i];
            list.add(new NameValuePair(type.toString(), type.value()));
        }

        return list;
    }
}
