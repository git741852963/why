package com.neusoft.features.session;

import com.neusoft.features.session.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;

public class EcHttpServletRequest extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(EcHttpServletRequest.class);
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private String sessionCookieName;
    private String cookieDomain;
    private String cookieContextPath;
    private int maxInactiveInterval;
    private DistributedSessionManager sessionManager;
    private DistributedSession distributedSession;
    private int cookieMaxAge;

    public EcHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request);
        this.request = request;
        this.response = response;
        this.sessionManager = DistributedSessionManager.instance();
    }

    public HttpSession getSession(boolean create) {
        return doGetSession(create);
    }

    public HttpSession getSession() {
        return doGetSession(true);
    }

    public String getSessionCookieName() {
        return this.sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookieContextPath() {
        return this.cookieContextPath;
    }

    public void setCookieContextPath(String cookieContextPath) {
        this.cookieContextPath = cookieContextPath;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public DistributedSession currentSession() {
        return this.distributedSession;
    }

    private HttpSession doGetSession(boolean create) {
        if (this.distributedSession == null) {
            Cookie cookie = WebUtil.findCookie(this, getSessionCookieName());
            if (cookie != null) {
                String value = cookie.getValue();
                log.debug("[SESSION] Find session`s id from cookie.[{}]", value);
                //TODO:这里应该重置cookie过期时间吧！否则可能过期
                //TODO:如果设定了cookieMaxAge，才需要设定，如果是关闭浏览器失效，则没必要
                this.distributedSession = buildDistributedSession(value, false);
            } else {
                this.distributedSession = buildDistributedSession(create);
            }
        } else {
            log.debug("[SESSION] Session[{}] was existed.", this.distributedSession.getId());
        }
        return this.distributedSession;
    }

    private DistributedSession buildDistributedSession(String sessionId, boolean cookie) {
        DistributedSession session = new DistributedSession(this.sessionManager, this.request, sessionId);
        session.setMaxInactiveInterval(this.maxInactiveInterval);
        if (cookie) {
            WebUtil.addCookie(this, this.response, getSessionCookieName(), sessionId, getCookieDomain(), getCookieContextPath(), this.cookieMaxAge, true);
        }
        return session;
    }

    private DistributedSession buildDistributedSession(boolean create) {
        if (create) {
            this.distributedSession = buildDistributedSession(this.sessionManager.getSessionIdGenerator().generateId(this.request), true);
            log.debug("[SESSION] Build new session[{}].", this.distributedSession.getId());
            return this.distributedSession;
        }
        return null;
    }
}