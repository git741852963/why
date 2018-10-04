package com.neusoft.dxz.web.security.interceptors;

import com.neusoft.dxz.web.security.AuthItem;
import com.neusoft.dxz.web.security.AuthProvider;
import com.neusoft.dxz.web.security.WhiteItem;
import com.neusoft.dxz.module.user.demo.exception.UnAuthorize401Exception;
import com.neusoft.dxz.module.user.demo.exception.UserNotLoginException;
import com.neusoft.features.user.base.BaseUser;
import com.neusoft.features.user.base.UserUtil;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * AuthInterceptor
 *
 * @author andy.jiao@msn.com
 */
public class AuthInterceptor extends AbstractUrlBasedInterceptor {

    private Set<WhiteItem> whiteList;
    private Set<AuthItem> protectList;

    @Autowired
    public AuthInterceptor(@Qualifier("DBAuthProviderImpl") AuthProvider authProvider) throws IOException {
        if (authProvider == null) {
            protectList = Sets.newConcurrentHashSet();
            whiteList = Sets.newConcurrentHashSet();
        } else {
            protectList = authProvider.getProtectList();
            whiteList = authProvider.getWhiteList();
        }
    }

    /**
     * 判断用户是否有权限进行访问。
     *
     * @param request request object
     * @param response response object
     * @param handler handler object
     * @return 用户权限是否满足
     */
    public boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestUri = request.getRequestURI().substring(request.getContextPath().length());
        BaseUser user = UserUtil.getCurrentUser();

        String method = request.getMethod().toLowerCase();

        // 1.在白名单里的，无条件放行
        for (WhiteItem whiteItem : whiteList) {
            // method and uri matches with white list, ok
            if (whiteItem.httpMethods.contains(method) && whiteItem.pattern.matcher(requestUri).matches()) {
                log.debug("[INTERCEPTOR] url match whitelist, url:{}, method:{}", requestUri, method);
                return true;
            }
        }

//        // 2.不在白名单里，需要用户登录
//        if (user == null) {
//            // 用户未登陆，抛出异常
//            throw new UserNotLoginException();
//        }
//
//        // 3.如果在受保护列表里，需要鉴权（超级管理员直接放行）
//        if (!user.isSuperMan()) {
//            for (AuthItem authItem : protectList) {
//                if (authItem.pattern.matcher(requestUri).matches()) {
//                    if (roleMatch(authItem.roles, user.getRoles())) {
//                        //用户角色匹配
//                        return true;
//                    } else {
//                        //能进入这里的,说明接口受到保护,用户已登陆,但用户角色不匹配,因此抛出鉴权失败的异常
//                        throw new UnAuthorize401Exception("您无权进行此操作");
//                    }
//                }
//            }
//        }

        return true;
    }

    /**
     * 判断用户类型是否匹配。
     *
     * @param expectedType 预期类型
     * @param actualType 用户类型
     * @return 用户类型是否匹配
     */
    private boolean typeMatch(Set<Integer> expectedType, Integer actualType) {
        return expectedType.contains(actualType);
    }

    /**
     * 判断用户角色是否匹配。
     *
     * @param expectedRoles 角色集合
     * @param actualRoles 当前用户角色列表
     * @return 用户角色是否匹配
     */
    private boolean roleMatch(Set<String> expectedRoles, List<Long> actualRoles) {
        //TODO:这里不对，应该是按名字匹配，总不能让权限列表里写角色ID吧
        for (Long actualRole : actualRoles) {
            if (expectedRoles.contains(actualRole.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用
     * <p/>
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception ex) throws Exception {
    }
}
