package com.neusoft.features.db.annotation.enums;

/**
 * DataSourceElement
 *
 * @author andy.jiao@msn.com
 */
public enum DataSourceElement {

    MASTER_00("master00"),
    SLAVE_00("slave00"),
    MASTER_10("master10"),
    SLAVE_10("slave10"),
    MASTER_20("master20"),
    SLAVE_20("slave20");

    private final String value;

    private DataSourceElement(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
