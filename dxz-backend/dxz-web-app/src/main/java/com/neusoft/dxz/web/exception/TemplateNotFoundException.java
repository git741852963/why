package com.neusoft.dxz.web.exception;

import com.neusoft.features.exception.CustomRuntimeException;

/**
 * 模板未找到异常。
 *
 * @author andy.jiao@msn.com
 */
public class TemplateNotFoundException extends CustomRuntimeException {

    private static final long serialVersionUID = 1L;

    public TemplateNotFoundException() {
        super("模板未找到");
    }

    public TemplateNotFoundException(Throwable cause) {
        super(cause);
    }

    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }
}
