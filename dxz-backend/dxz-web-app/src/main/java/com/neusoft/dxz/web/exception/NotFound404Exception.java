package com.neusoft.dxz.web.exception;

import com.neusoft.features.common.annotation.ResponseCode;

/**
 * open api/controller api未找到异常。
 *
 * @author andy.jiao@msn.com
 */
//@ResponseStatus(HttpStatus.NOT_FOUND)
@ResponseCode(value = "E00001", reason = "common.error.e00001")
public class NotFound404Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFound404Exception() {
    }

    public NotFound404Exception(Throwable cause) {
        super(cause);
    }

    public NotFound404Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFound404Exception(String message) {
        super(message);
    }
}
