package com.neusoft.dxz.module.user.demo.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.exception.CustomRuntimeException;

@ResponseCode(value="E10000", reason="common.error.e10000")
public class UserNotLoginException extends CustomRuntimeException {
    public UserNotLoginException() {
    }

    public UserNotLoginException(String message) {
        super(message);
    }

    public UserNotLoginException(Throwable cause) {
        super(cause);
    }

    public UserNotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
