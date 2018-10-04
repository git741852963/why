package com.neusoft.features.session.redis;

import com.neusoft.features.session.SerializeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSerializer {
    private static final Logger log = LoggerFactory.getLogger(JsonSerializer.class);
    private final ObjectMapper objectMapper;
    private final JavaType javaType;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.javaType = this.objectMapper.getTypeFactory().constructParametricType(Map.class, new Class[]{String.class, Object.class});
    }

    public String serialize(Object o) {
        try {
            return this.objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("failed to serialize http session {} to json,cause:{}", o, Throwables.getStackTraceAsString(e));
            throw new SerializeException("failed to serialize http session to json", e);
        }
    }

    public Map<String, Object> deserialize(String o) {
        try {
            return (Map) this.objectMapper.readValue(o, this.javaType);
        } catch (Exception e) {
            log.error("failed to deserialize string  {} to http session,cause:{} ", o, Throwables.getStackTraceAsString(e));
            throw new SerializeException("failed to deserialize string to http session", e);
        }
    }
}