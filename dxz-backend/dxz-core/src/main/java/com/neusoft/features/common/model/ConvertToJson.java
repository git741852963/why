package com.neusoft.features.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ConvertToJson {
    String toJson();

    String toJsonNonNull() throws JsonProcessingException;
}
