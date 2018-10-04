package com.neusoft.features.db.annotation.enums;

/**
 * GeneratedItem.java
 *
 * @author andy.jiao@msn.com
 */
public enum GeneratedItem {

    CREATEAT("CREATEAT", "CREATEAT"),
    UPDATEAT("UPDATEAT", "UPDATEAT"),
    ISDELETE("ISDELETE", "ISDELETE");

    private final String value;

    private final String description;

    private GeneratedItem(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return description;
    }
}
