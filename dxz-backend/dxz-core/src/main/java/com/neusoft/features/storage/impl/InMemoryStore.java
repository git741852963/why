package com.neusoft.features.storage.impl;

import com.neusoft.features.storage.Storable;
import com.neusoft.features.storage.Storage;
import com.google.common.collect.Maps;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component("inMemoryStore")
public class InMemoryStore
        implements Storage {
    private static Map<String, Storable> data = Maps.newHashMap();

    public Storable get(String key) {
        return (Storable) data.get(key);
    }

    public boolean put(Storable storable) {
        data.put(storable.key(), storable);
        return true;
    }

    public void remove(String key) {
        data.remove(key);
    }
}
