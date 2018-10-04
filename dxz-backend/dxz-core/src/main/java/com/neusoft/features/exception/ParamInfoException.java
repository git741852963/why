package com.neusoft.features.exception;

public class ParamInfoException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public ParamInfoException() {
    }

    public ParamInfoException(String message) {
        super(message);
    }

    public ParamInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamInfoException(Throwable cause) {
        super(cause);
    }
}