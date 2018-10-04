package com.neusoft.features.exception;

/**
 * 创建文件失败异常。
 *
 * @author andy.jiao@msn.com
 */
public class FileCreateException extends CustomRuntimeException {
    private static final long serialVersionUID = 1L;

    public FileCreateException() {
    }

    public FileCreateException(String message) {
        super(message);
    }

    public FileCreateException(Throwable cause) {
        super(cause);
    }

    public FileCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}