package com.neusoft.features.common.dto;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.features.common.model.ConvertToJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * @author andy.jiao@msn.com
 */
public class BaseData implements ConvertToJson, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    @Override
    public String toJsonNonNull() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(this);
    }
}
