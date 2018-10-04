package com.neusoft.dxz.module.user.demo.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.exception.CustomRuntimeException;

@ResponseCode(value="E10003", reason="common.error.e10003")
public class UserNotFoundException extends CustomRuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
