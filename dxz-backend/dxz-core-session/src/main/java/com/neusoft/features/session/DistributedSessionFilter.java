package com.neusoft.features.session;

import com.neusoft.features.session.constants.SessionConstants;
import com.neusoft.features.session.util.WebUtil;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 分布式session filter。
 *
 * @author andy.jiao@msn.com
 */
public class DistributedSessionFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(DistributedSessionFilter.class);

    /**
     * 分布式session管理器
     */
    private final DistributedSessionManager sessionManager;

    /**
     * session在cookie中的key
     */
    private String sessionCookieName;

    /**
     * session过期时间
     */
    private int maxInactiveInterval;

    /**
     * cookie域名
     */
    private String cookieDomain;

    /**
     * cookie路径
     */
    private String cookieContextPath;

    /**
     * cookie过期时间
     */
    private int cookieMaxAge;

    /**
     * 构造函数，获取sessionManager实例
     */
    public DistributedSessionFilter() {
        this.sessionManager = DistributedSessionManager.instance();
    }

    /**
     * 初始化
     *
     * @param filterConfig web.xml中配置的filter参数
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            initParameters(filterConfig);
        } catch (Exception ex) {
            log.error("[SESSION] failed to init cache session filter", ex);
            throw new ServletException(ex);
        }
    }

    /**
     * 使用自定义的EcHttpServletRequest包装request。
     *
     * @param request  request object
     * @param response response object
     * @param chain    filter chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof EcHttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 包装为EcHttpServletRequest
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        EcHttpServletRequest ecRequest = new EcHttpServletRequest(httpRequest, httpResponse);
        ecRequest.setSessionCookieName(this.sessionCookieName);
        ecRequest.setMaxInactiveInterval(this.maxInactiveInterval);
        ecRequest.setCookieDomain(this.cookieDomain);
        ecRequest.setCookieContextPath(this.cookieContextPath);
        ecRequest.setCookieMaxAge(this.cookieMaxAge);

        // 放行，执行filter chain下一步处理
        chain.doFilter(ecRequest, response);

        // 服务器响应进行后处理
        // 获取session
        DistributedSession session = ecRequest.currentSession();
        // session过期/更新处理
        if (session != null) {
            if (!session.isValid()) {
                // session非法（用户退出）
                log.debug("[SESSION] delete login cookie");
                WebUtil.failureCookie(httpRequest, httpResponse, this.sessionCookieName, this.cookieDomain, this.cookieContextPath);
            } else if (session.isDirty()) {
                // session更新（属性变更），保存到缓存
                log.debug("[SESSION] try to flush session to session store");
                Map<String, Object> snapshot = session.snapshot();
                if (this.sessionManager.save(session.getId(), snapshot, this.maxInactiveInterval)) {
                    log.debug("[SESSION] succeed to flush session {} to store, key is:{}", snapshot, session.getId());
                } else {
                    log.error("[SESSION] failed to save session to redis");
                    WebUtil.failureCookie(httpRequest, httpResponse, this.sessionCookieName, this.cookieDomain, this.cookieContextPath);
                }
            } else {
                // 更新过期时间
                this.sessionManager.refreshExpireTime(session, this.maxInactiveInterval);
            }
        }
    }

    /**
     * 获取配置参数。
     *
     * @param filterConfig web.xml中配置的filter参数
     */
    private void initParameters(FilterConfig filterConfig) {
        String sessionCookieNameParameter = SessionConstants.SESSION_COOKIE_NAME_KEY;
        String maxInactiveIntervalParameter = SessionConstants.MAX_INACTIVE_INTERVAL_KEY;
        String cookieDomainParameter = SessionConstants.COOKIE_DOMAIN_KEY;
        String cookieContextPathParameter = SessionConstants.COOKIE_CONTEXT_PATH_KEY;

        String temp = filterConfig.getInitParameter(sessionCookieNameParameter);
        this.sessionCookieName = (temp == null ? SessionConstants.DEFAULT_SESSION_COOKIE_NAME : temp);

        // 服务端session过期时间
        temp = filterConfig.getInitParameter(maxInactiveIntervalParameter);
        this.maxInactiveInterval = (temp == null ? SessionConstants.DEFAULT_MAX_INACTIVE_INTERVAL : Integer.valueOf(temp).intValue());

        this.cookieDomain = filterConfig.getInitParameter(cookieDomainParameter);

        temp = filterConfig.getInitParameter(cookieContextPathParameter);
        this.cookieContextPath = (temp == null ? "/" : temp);

        // 客户端cookie过期时间。如果不设定cookie超时时间，默认关闭浏览器失效
        this.cookieMaxAge = Integer.parseInt(MoreObjects.firstNonNull(filterConfig.getInitParameter(SessionConstants.COOKIE_MAX_AGE_KEY), "-1"));
        log.info("[SESSION] initialize session filter： (sessionCookieName={},maxInactiveInterval={},cookieDomain={})", new Object[]{this.sessionCookieName, Integer.valueOf(this.maxInactiveInterval), this.cookieDomain});
    }

    /**
     * 销毁方法。
     */
    public void destroy() {
        this.sessionManager.destroy();
    }
}
