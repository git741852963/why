package com.neusoft.dxz.web.security.interceptors;

import com.neusoft.dxz.module.user.demo.model.User;
import com.neusoft.dxz.module.user.demo.service.UserService;
import com.neusoft.features.api.constants.ApiHeaderConstants;
import com.neusoft.features.common.model.Response;
import com.neusoft.dxz.module.user.demo.exception.UserNotFoundException;
import com.neusoft.features.user.base.UserUtil;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * api接口拦截器。
 * <p/>
 * 用户信息保存到threadlocal。
 *
 * @author andy.jiao@msn.com
 */
public class ApiLoginInterceptor extends AbstractUrlBasedInterceptor {

    @Autowired
    private UserService userService;

    @Value("#{configProperties['app.auth.secret.key']}")
    private String secretKey;

    /**
     * 如果用户已登录且token合法，获取当前登录用户信息并保存到ThreadLocal。
     *
     * @param request  request object
     * @param response response object
     * @param handler  handler object
     * @return 是否执行下一个拦截器
     */
    @Override
    public boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {

        final String token = request.getHeader(ApiHeaderConstants.X_ACCESS_TOKEN);

        //TODO:token应该保存到redis，现在直接无限制登录了，应该利用redis使用过期时间
        //TODO:或者使用jwt的iss，这样jwt不用保存到redis，现在使用这种方法
        //TODO:jwt应有宽限时间

        // token存在
        if (!Strings.isNullOrEmpty(token)) {

            log.debug("[TOKEN]：get token: {}", token);

            // 获取用户信息并保存ThreadLocal
            final Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

            // 过期时间判断
            DateTime expDate = new DateTime(claims.getExpiration().getTime());

            log.debug("[TOKEN] token to expire: {} seconds, ({}) ", expDate.minus(DateTime.now().getMillis()).getMillis(), expDate.toString("yyyy/MM/dd HH:mm:ss"));
            if (!expDate.isBeforeNow()) {
                // 即使过期也不要抛出异常
//                throw new UserNotLoginException();

                //TODO:其他判断

                // 获取用户信息
                String userId = claims.getSubject();
                Response<User> result = userService.findById(Long.valueOf(userId));
                if (!result.isSuccess()) {
                    log.error("[TOKEN]：failed to find user where id={}, error:{}", userId, result.getError());
                    throw new UserNotFoundException();
                }

                // 去除敏感信息，保存到threadlocal
                User u = result.getResult();
                u.setPassword("");
                UserUtil.putCurrentUser(result.getResult());

//            if (Strings.isNullOrEmpty(userId)) {
//                log.debug("[TOKEN]：guest user, isGuest={}", claims.get("isGuest"));
//            } else {
//                Response<User> result = userService.findById(Long.valueOf(userId));
//                if (!result.isSuccess()) {
//                    log.error("[TOKEN]：failed to find user where id={}, error:{}", userId, result.getError());
//                    throw new UserNotFoundException();
//                }
//
//                UserUtil.putCurrentUser(result.getResult());
//            }
            }
        } else {
            UserUtil.removeUser();
        }

        return true;
    }
}
