package com.neusoft.features.redis.utils;

import com.neusoft.features.common.utils.JsonMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringHashMapper<T> {
    private final JsonMapper mapper;
    private final JavaType userType;
    private final JavaType mapType;

    public StringHashMapper(Class<T> type) {
        this.mapper = JsonMapper.nonDefaultMapper();
        this.mapType = this.mapper.createCollectionType(HashMap.class, new Class[]{String.class, String.class});
        this.userType = this.mapper.getMapper().getTypeFactory().constructType(type);
    }

    public T fromHash(Map<String, String> hash) {
        return this.mapper.getMapper().convertValue(hash, this.userType);
    }

    public Map<String, String> toHash(T object) {
        Map<String, String> hash = this.mapper.getMapper().convertValue(object, this.mapType);

        List<String> nullKeys = Lists.newArrayListWithCapacity(hash.size());
        for (String key : hash.keySet()) {
            if (hash.get(key) == null) {
                nullKeys.add(key);
            }
        }
        for (String nullKey : nullKeys) {
            hash.remove(nullKey);
        }
        return hash;
    }
}