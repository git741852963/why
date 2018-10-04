package com.neusoft.features.common.enums;

/**
 * 
 * FileCategoryEnums
 *
 * @author <A>cheng.yy@neusoft.com</A>
 *
 * @date Jul 18, 2015
 *
 * @Copyright: © 2001-2015 东软集团股份有限公司
 *
 */
public enum FileType {

    IMAGE("image", "image"), VIDEO("video","video"), ORTHER("other", "other");

    private final String value;

    private final String description;

    private FileType(String value, String description) {
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
