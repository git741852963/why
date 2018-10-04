package com.neusoft.features.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Name Value Pair
 *
 * @author andy.jiao@msn.com
 */
@AllArgsConstructor
@Data
public class NameValuePair implements Serializable {

    private static final long serialVersionUID = 1L;

    public NameValuePair() {

    }

    private String name;
    private Object value;
}