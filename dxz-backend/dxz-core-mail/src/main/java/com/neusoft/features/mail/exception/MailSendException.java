package com.neusoft.features.mail.exception;

import com.neusoft.features.exception.CustomRuntimeException;

public class MailSendException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public MailSendException() {
    }

    public MailSendException(String message) {
        super(message);
    }

    public MailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailSendException(Throwable cause) {
        super(cause);
    }
}