package com.neusoft.features.api.enums;

/**
 * Base64编码类型。
 *
 * @author andy.jiao@msn.com
 */
public enum Base64MediaType {

    PNG("image/png", "png"),
    JPG("image/jpg", "jpg"),
    JPEG("image/jpeg", "jpeg"),
    TIFF("image/tiff", "tiff"),
    GIF("image/gif", "gif");

    private final String value;

    private final String base64Media;

    private Base64MediaType(String value, String base64Media) {
        this.value = value;
        this.base64Media = base64Media;
    }

    public static Base64MediaType from(String value) {
        for (Base64MediaType mediaType : Base64MediaType.values()) {
            if (mediaType.value().equalsIgnoreCase(value)) {
                return mediaType;
            }
        }
        return null;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return base64Media;
    }
}
