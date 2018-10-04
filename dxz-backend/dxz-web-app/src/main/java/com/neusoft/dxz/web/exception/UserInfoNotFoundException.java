package com.neusoft.dxz.web.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.exception.CustomRuntimeException;

/**
 * 用户信息未找到。
 *
 * @author andy.jiao@msn.com
 */
@ResponseCode(value="E00003", reason="common.error.e00003")
public class UserInfoNotFoundException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public UserInfoNotFoundException() {
        super("用户信息未找到");
    }

    public UserInfoNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserInfoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInfoNotFoundException(String message) {
        super(message);
    }
}
