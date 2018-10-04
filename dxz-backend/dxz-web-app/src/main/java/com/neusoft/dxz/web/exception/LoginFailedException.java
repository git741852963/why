package com.neusoft.dxz.web.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.exception.CustomRuntimeException;

/**
 * 登录失败异常。
 *
 * @author andy.jiao@msn.com
 */
@ResponseCode(value="E10002", reason="common.error.e10002")
public class LoginFailedException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public LoginFailedException() {
    }

    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(Throwable cause) {
        super(cause);
    }

    public LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
