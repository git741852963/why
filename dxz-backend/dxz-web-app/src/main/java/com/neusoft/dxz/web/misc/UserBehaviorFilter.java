package com.neusoft.dxz.web.misc;

import com.alibaba.fastjson.JSON;
import com.neusoft.features.session.util.WebUtil;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserBehaviorFilter extends HttpServlet implements Filter {

    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(UserBehaviorFilter.class);

    private final String CONFIG_KEY_INTERCEPT_REGEX = "interceptRegex";
    private final String CONFIG_KEY_EXCLUE_REGEX = "exclueRegex";

    private final String DEFAULT_INTERCEPT_REGEX = "(?<=/)[^/].*([/]$|[^/]$|\\.(action|json|form|do|jsp|hbs|htm)$)";
    private final String DEFAULT_EXCLUE_REGEX = "(?<=/)[^/].*\\.(html|js|css|png|jpg|gif|pdf|txt|zip)$";

    private final String LOG_CLIENT_IP = "Client IP";
    private final String LOG_PROXY = "Proxy";
    private final String LOG_REQUEST_URL = "Request Url";
    private final String LOG_METHOD = "Method";
    private final String LOG_PARAM = "Param";
    private final String LOG_REQUEST_BODY = "Request Body";

    private Pattern interceptPattern = null;
    private Pattern excludePattern = null;

    @Override
    public void destroy() {
        // TODO
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // 过滤、验证并保存用户请求拦截信息
        doSaveBehavior(req);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String interceptRegex = MoreObjects.firstNonNull(filterConfig.getInitParameter(CONFIG_KEY_INTERCEPT_REGEX), DEFAULT_INTERCEPT_REGEX);
        String excludeRegex = MoreObjects.firstNonNull(filterConfig.getInitParameter(CONFIG_KEY_EXCLUE_REGEX), DEFAULT_EXCLUE_REGEX);

        interceptPattern = Pattern.compile(interceptRegex);
        excludePattern = Pattern.compile(excludeRegex);

        HashMap<String, Object> debug = new HashMap<>();
        debug.put("INTERCEPT", interceptRegex);
        debug.put("EXCLUDE", excludeRegex);
        logger.debug("UserBehaviorFilter:" + JSON.toJSONString(debug));
    }

    /**
     * 拦截用户请求
     */
    private void doSaveBehavior(HttpServletRequest request) {

        // 获取用户请求路径
        String requestUrl = request.getRequestURL().toString();

        logger.debug(">request Url:" + requestUrl);

        if (!interceptPattern.matcher(requestUrl).matches()) {
            // 不匹配拦截规则
            logger.debug(">request url not match");
            return;
        }
        if (excludePattern.matcher(requestUrl).matches()) {
            // 匹配排除规则
            logger.debug(">request url not match");
            return;
        }

        logger.debug(">request url and intercept regex matched");

        HashMap<String, String> userBehavior = new HashMap<>();
        userBehavior.put(LOG_CLIENT_IP, WebUtil.getIpAddrMultiNginx(request));
        userBehavior.put(LOG_PROXY, request.getRemoteAddr());
        userBehavior.put(LOG_REQUEST_URL, requestUrl);
        userBehavior.put(LOG_METHOD, request.getMethod());
        userBehavior.put(LOG_PARAM, request.getQueryString());
        userBehavior.put(LOG_REQUEST_BODY, this.buildXml(request));

        logger.info(JSON.toJSONString(userBehavior));
    }

    private boolean match(Pattern pattern, String url) {
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * 解析请求body
     */
    @SuppressWarnings("rawtypes")
    private String buildXml(HttpServletRequest request) {
        StringBuffer xml = new StringBuffer();
        Enumeration names = request.getParameterNames();
        xml.append("<?xml version=\"1.0\" standalone=\"yes\"?>");
        xml.append("<request>");
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            xml.append("<").append(name).append(">").append(request.getParameter(name)).append("</").append(name).append(">");
        }
        xml.append("</request>");
        return xml.toString();
    }

}
