package com.neusoft.features.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapBuilder<K, V> {
    private Map<K, V> map;
    private boolean ignoreNullValue = false;

    private MapBuilder() {
        this.map = new HashMap();
    }

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> of() {
        return new MapBuilder();
    }

    public static <K, V> MapBuilder<K, V> of(Map<K, V> map) {
        return new MapBuilder(map);
    }

    public static <K, V> MapBuilder<K, V> newHashMap() {
        return of(new HashMap());
    }

    public static <K, V> MapBuilder<K, V> newTreeMap() {
        return of(new TreeMap());
    }

    public MapBuilder<K, V> ignoreNullValue() {
        this.ignoreNullValue = true;
        return this;
    }

    public MapBuilder<K, V> put(K key, V value) {
        if ((this.ignoreNullValue) && (value == null)) {
            return this;
        }
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> map() {
        return this.map;
    }
}

