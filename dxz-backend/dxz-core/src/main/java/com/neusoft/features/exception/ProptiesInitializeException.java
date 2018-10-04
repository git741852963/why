package com.neusoft.features.exception;

public class ProptiesInitializeException extends CustomRuntimeException {

    private static final long serialVersionUID = 1L;

    public ProptiesInitializeException() {
    }

    public ProptiesInitializeException(String message) {
        super(message);
    }

    public ProptiesInitializeException(Throwable cause) {
        super(cause);
    }

    public ProptiesInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}