package com.neusoft.dxz.web.exception;

/**
 * 页面渲染异常。
 *
 * @author andy.jiao@msn.com
 */
public class RenderException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RenderException() {
    }

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }
}
