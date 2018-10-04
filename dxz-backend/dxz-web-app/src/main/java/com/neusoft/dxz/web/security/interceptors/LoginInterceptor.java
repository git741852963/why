package com.neusoft.dxz.web.security.interceptors;

import com.neusoft.dxz.module.user.demo.exception.UserNotFoundException;
import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.dxz.module.user.demo.service.UserRoleService;
import com.neusoft.dxz.module.user.demo.service.UserService;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.session.constants.SessionConstants;
import com.neusoft.dxz.web.exception.UserInfoNotFoundException;
import com.neusoft.features.user.base.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户信息拦截器。
 * <p/>
 * 用户信息保存到threadlocal。
 *
 * @author andy.jiao@msn.com
 */
public class LoginInterceptor extends AbstractUrlBasedInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 如果用户已登录且session合法，获取当前登录用户信息并保存到threadlocal。
     *
     * @param request request object
     * @param response response object
     * @param handler handler object
     * @return 是否执行下一个拦截器
     */
    @Override
    public boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute(SessionConstants.SESSION_USER_ID);
            if (userId != null) {

                Response<User> result = userService.findById(Long.valueOf(userId.toString()));
                if (!result.isSuccess()) {
                    log.error("failed to find user, user id={}, error code:{}", userId, result.getError());
                    throw new UserNotFoundException();
                }

                User user = result.getResult();

                //TODO:抛出的异常需要处理一下，不太符合
                Response<List<Long>> rolesResult = userRoleService.findRoleIdsByUserId(Long.parseLong(userId.toString()));
                if (!rolesResult.isSuccess()) {
                    log.error("failed to find user roles, user id={}, error code:{}", userId, rolesResult.getError());
                    throw new UserInfoNotFoundException();
                }

                user.setRoles(rolesResult.getResult());

                UserUtil.putCurrentUser(result.getResult());
            }
        } else {
            //TODO:保险起见，确保线程内没有保存用户信息
            UserUtil.removeUser();
            UserUtil.clearCookies();
        }

        return true;
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
        UserUtil.removeUser();
        UserUtil.clearCookies();
    }
}
