package com.neusoft.features.session.exception;

import com.neusoft.features.exception.CustomRuntimeException;

public class SessionException extends CustomRuntimeException {
    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }
}