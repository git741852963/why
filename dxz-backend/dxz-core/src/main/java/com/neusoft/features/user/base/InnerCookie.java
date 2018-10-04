package com.neusoft.features.user.base;

import java.io.Serializable;
import java.util.Map;

public class InnerCookie
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> cookies;

    public InnerCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String get(String name) {
        return (String) this.cookies.get(name);
    }
}