package com.neusoft.features.session;

import javax.servlet.http.HttpServletRequest;

/**
 * session id生成器接口。
 *
 * @author andy.jiao@msn.com
 */
public abstract interface SessionIdGenerator {
    public abstract String generateId(HttpServletRequest paramHttpServletRequest);
}
