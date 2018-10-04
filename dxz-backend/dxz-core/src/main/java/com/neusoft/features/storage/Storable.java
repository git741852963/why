package com.neusoft.features.storage;

import java.io.Serializable;

public class Storable
        implements Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    protected final Object target;
    private final Long expiredSeconds;

    public Storable(String key, Object target, Long expiredSeconds) {
        this.key = key;
        this.target = target;
        if (expiredSeconds.longValue() > 0L) {
            this.expiredSeconds = Long.valueOf(System.currentTimeMillis() + expiredSeconds.longValue() * 1000L);
        } else {
            this.expiredSeconds = Long.valueOf(-1L);
        }
    }

    public boolean isExpired() {
        return (this.expiredSeconds.longValue() != -1L) && (System.currentTimeMillis() > this.expiredSeconds.longValue());
    }

    public String key() {
        return this.key;
    }
}