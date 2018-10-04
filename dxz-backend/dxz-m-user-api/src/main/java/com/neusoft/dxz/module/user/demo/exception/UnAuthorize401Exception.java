package com.neusoft.dxz.module.user.demo.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 用户无权限异常。
 *
 * @author andy.jiao@msn.com
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@ResponseCode(value="E10001", reason="common.error.e10001")
public class UnAuthorize401Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnAuthorize401Exception() {
    }

    public UnAuthorize401Exception(String message) {
        super(message);
    }

    public UnAuthorize401Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorize401Exception(Throwable cause) {
        super(cause);
    }
}
