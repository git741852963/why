package com.neusoft.dxz.web.exception;

import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.common.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 服务器内部错误。
 *
 * @author andy.jiao@msn.com
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ResponseCode(value="E20000", reason="common.error.e20000")
public class Server500Exception extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(Server500Exception.class);
    private static final long serialVersionUID = 1L;

    public Server500Exception() {
    }

    public Server500Exception(String message) {
        super(message);
    }

    public Server500Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Server500Exception(Throwable cause) {
        super(cause);
    }

    public static void failToThrow(Response<?> response) {
        failToThrow(response, (String)null, new Object[0]);
    }

    public static void failToThrow(Response<?> response, String logInfo, Object... args) {
        if(!response.isSuccess()) {
            if(logInfo != null) {
                log.error(logInfo, args);
            }

            log.error("error happened: {}", response.getError());
            throw new Server500Exception(response.getError());
        }
    }
}
