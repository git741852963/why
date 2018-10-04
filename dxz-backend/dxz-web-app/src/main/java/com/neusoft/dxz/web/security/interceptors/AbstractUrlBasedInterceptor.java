package com.neusoft.dxz.web.security.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * AbstractUrlBasedInterceptor
 *
 * @author andy.jiao@msn.com
 */
public abstract class AbstractUrlBasedInterceptor extends HandlerInterceptorAdapter {

    /** log */
    protected final Logger log ;

    public AbstractUrlBasedInterceptor() {
        log =  LoggerFactory.getLogger(getClass().getName());
    }

    protected String interceptRegex;
    protected String excludeRegex;
    protected Pattern includePattern;
    protected Pattern excludePattern;

    /**
     * 取得包含url正则表达式
     *
     * @return 包含url正则表达式
     */
    public String getInterceptRegex() {
        return interceptRegex;
    }

    /**
     * 设置包含url正则表达式
     */
    public void setInterceptRegex(String include) {
        this.interceptRegex = include;
        includePattern = Pattern.compile(include);
    }

    /**
     * 取得排除url正则表达式
     *
     * @return 排除url正则表达式
     */
    public String getExcludeRegex() {
        return excludeRegex;
    }

    /**
     * 设置排除url正则表达式
     */
    public void setExcludeRegex(String exclude) {
        this.excludeRegex = exclude;
        excludePattern = Pattern.compile(exclude);
    }

    /**
     * 在业务处理器处理请求之前被调用。
     * <p/>
     * 如果返回false，从当前的拦截器往回执行所有拦截器的afterCompletion()，再退出拦截器链。
     * 如果返回true，执行下一个拦截器，直到所有的拦截器都执行完毕，再执行被拦截的Controller，然后进入拦截器链，
     * 从最后一个拦截器往回执行所有的postHandle()，接着再从最后一个拦截器往回执行所有的afterCompletion()。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestUrl = request.getRequestURL().toString();
        log.debug("[INTERCEPTOR] request url:{}, include regex:{}, exclude regex:{}", requestUrl, this.interceptRegex, this.excludeRegex);

        if (includePattern != null && !includePattern.matcher(requestUrl).find()) {
            // 不匹配拦截规则
            return true;
        }

        if (excludePattern != null && excludePattern.matcher(requestUrl).find()) {
            // 匹配排除规则
            return true;
        }

        return doHandler(request, response, handler);
    }

    /**
     * 执行过滤器
     */
    public abstract boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler);
}
