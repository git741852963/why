package com.neusoft.features.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Strings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMapper {
    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    public static final JsonMapper JSON_NON_EMPTY_MAPPER = new JsonMapper(JsonInclude.Include.NON_EMPTY);
    public static final JsonMapper JSON_NON_DEFAULT_MAPPER = new JsonMapper(JsonInclude.Include.NON_DEFAULT);
    private ObjectMapper mapper;

    private JsonMapper(JsonInclude.Include include) {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(include);
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.registerModule(new GuavaModule());
    }

    public static JsonMapper nonEmptyMapper() {
        return JSON_NON_EMPTY_MAPPER;
    }

    public static JsonMapper nonDefaultMapper() {
        return JSON_NON_DEFAULT_MAPPER;
    }

    public String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
        }
        return null;
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (Strings.isNullOrEmpty(jsonString)) {
            return null;
        }
        try {
            return this.mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
        }
        return null;
    }

    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (Strings.isNullOrEmpty(jsonString)) {
            return null;
        }
        try {
            return this.mapper.readValue(jsonString, javaType);
        } catch (Exception e) {
            logger.warn("parse json string error:" + jsonString, e);
        }
        return null;
    }

    public JsonNode treeFromJson(String jsonString)
            throws IOException {
        return this.mapper.readTree(jsonString);
    }

    public <T> T treeToValue(JsonNode node, Class<T> clazz)
            throws JsonProcessingException {
        return this.mapper.treeToValue(node, clazz);
    }

    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public <T> T update(String jsonString, T object) {
        try {
            return this.mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }

    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    public void enableEnumUseToString() {
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }
}
