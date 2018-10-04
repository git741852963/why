package com.neusoft.features.exception;

import com.neusoft.features.common.ErrorCodeConstants;

public class CustomRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomRuntimeException() {
        super();
    }

    public CustomRuntimeException(Throwable cause) {
        super(cause);
    }

    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRuntimeException(String message) {
        super(message);
    }

    //TODO:现在使用注解了，没必要用ResponseCode方法返回了
    public String getResponseCode() {
        return ErrorCodeConstants.INTERNAL_ERROR;
    }
}

