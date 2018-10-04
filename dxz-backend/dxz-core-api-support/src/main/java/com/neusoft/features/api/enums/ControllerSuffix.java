package com.neusoft.features.api.enums;

/**
 * Controller Url后缀。
 *
 * @author andy.jiao@msn.com
 */
public enum ControllerSuffix {

    JSON("json", "json"),
    ACTION("action", "action"),
    DO("do", "do"),
    FORM("form", "form");

    private final String value;

    private final String description;

    private ControllerSuffix(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ControllerSuffix from(String value) {
        for (ControllerSuffix platform : ControllerSuffix.values()) {
            if (platform.value().equalsIgnoreCase(value)) {
                return platform;
            }
        }
        return null;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return description;
    }
}
