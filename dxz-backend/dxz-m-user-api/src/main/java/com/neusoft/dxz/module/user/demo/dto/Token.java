package com.neusoft.dxz.module.user.demo.dto;

import com.neusoft.features.common.dto.BaseDto;
import lombok.Data;

@Data
public class Token extends BaseDto {

    private static final long serialVersionUID = 1L;

    /**
     * token
     */
    private String token;

    /**
     * passportId
     */
    private String passportId;

    /**
     * 是否注册型登录
     */
    private Boolean signUpLogin;
}
