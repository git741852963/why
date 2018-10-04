package com.neusoft.dxz.web.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.exception.CustomRuntimeException;

/**
 * 登录失败次数超过限制，需要验证码。
 *
 * @author andy.jiao@msn.com
 */
@ResponseCode(value="E10004", reason="common.error.e10004")
public class RequireVerifyCodeException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public RequireVerifyCodeException() {
    }

    public RequireVerifyCodeException(String message) {
        super(message);
    }

    public RequireVerifyCodeException(Throwable cause) {
        super(cause);
    }

    public RequireVerifyCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
