package com.neusoft.features.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Name Value User
 *
 * @author andy.jiao@msn.com
 */
@AllArgsConstructor
@Data
public class NameValueUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public NameValueUser() {

    }

    private Long value;
    private String name;
    private Long userId;
}