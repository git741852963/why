package com.neusoft.features.db.annotation.enums;

/**
 * GeneratedItem.java
 *
 * @author andy.jiao@msn.com
 */
public enum GeneratedStrategy {

    DEFAULT("DEFAULT", "DEFAULT"),
    UUID("UUID", "UUID");

    private final String value;

    private final String description;

    private GeneratedStrategy(String value, String description) {
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
