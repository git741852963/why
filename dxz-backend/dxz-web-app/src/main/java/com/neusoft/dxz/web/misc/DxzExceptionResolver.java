package com.neusoft.dxz.web.misc;

import com.alibaba.fastjson.JSON;
import com.neusoft.dxz.module.user.demo.exception.UnAuthorize401Exception;
import com.neusoft.dxz.module.user.demo.exception.UserNotLoginException;
import com.neusoft.dxz.web.constants.RequestSuffix;
import com.neusoft.features.api.constants.ApiErrorCodeConstants;
import com.neusoft.features.api.enums.ControllerSuffix;
import com.neusoft.features.common.ErrorCodeConstants;
import com.neusoft.features.common.MessageSources;
import com.neusoft.features.common.annotation.ResponseCode;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.exception.CustomRuntimeException;
import com.neusoft.dxz.web.exception.TemplateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 全局错误处理。
 *
 * @author andy.jiao@msn.com
 */
public class DxzExceptionResolver extends ExceptionHandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(DxzExceptionResolver.class);

    @Autowired
    private MessageSources messageSources;

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {

        String requestUri = request.getRequestURI().substring(request.getContextPath().length());
        String requestSuffix = requestUri.indexOf(".") > 0 ? requestUri.substring(requestUri.lastIndexOf(".")) : "";
        requestSuffix = requestSuffix.length() > 1 ? requestSuffix.substring(1) : null;
        ControllerSuffix suffixEnum = requestSuffix == null ? null : ControllerSuffix.from(requestSuffix);

        if (requestUri.startsWith(RequestSuffix.PREFIX_API)) {
            logger.error("[*] api unsolved exception: path={}, exception={}{}{}{}", requestUri, "[" , exception.getClass().getName() , "]:", exception.getLocalizedMessage());

            // api error
            if (requestUri.matches(RequestSuffix.SUFFIX_FORM)) {
                // api:form提交
                Response resp = new Response();
                resp.setError(messageSources.get(exception.getLocalizedMessage()));
                return new ModelAndView("resource:/error-form", "error", resp);
            } else {
                // api:用户未登录 用户权限 其他异常
                responseJson(response, exception);
                return null;
            }
        } else if (suffixEnum != null) {
            logger.error("[*] controller unsolved exception: path={}, exception={}{}{}{}", requestUri, "[" , exception.getClass().getName() , "]:", exception.getLocalizedMessage());
            // controller error
            switch (suffixEnum) {
                case JSON:
                    responseJson(response, exception);
                    break;
                case ACTION:
                    responseJson(response, exception);
                    break;
                case DO:
                    responseJson(response, exception);
                    break;
                case FORM:
                    Response resp = new Response();
                    resp.setError(messageSources.get(exception.getLocalizedMessage()));
                    return new ModelAndView("resource:/error-form", "error", resp);
                default:
                    responseJson(response, exception);
                    break;
            }
            return null;
        } else {
            logger.error("[*] view unsolved exception: path={}, exception={}{}{}{}", requestUri, "[" , exception.getClass().getName() , "]:", exception.getLocalizedMessage());
            // view error
            if (exception instanceof TemplateNotFoundException) {
                // 模板未找到
                return new ModelAndView("resource:/404");
            } else if (exception instanceof UserNotLoginException) {
                // 用户未登录
                String gotoUrl = generateQueryString(request, requestUri);
                return new ModelAndView("login", "_GOTO_", gotoUrl);
            } else if (exception instanceof UnAuthorize401Exception) {
                // 用户无权限
                return new ModelAndView("resource:/401");
            } else {
                // 其他错误
                return new ModelAndView("resource:/500");
            }
        }
    }

    private String generateQueryString(HttpServletRequest request, String requestUrl) {
        // url 参数处理
        Map<String, String[]> params = request.getParameterMap();

        StringBuilder buffer = new StringBuilder();
        buffer.append(requestUrl).append("?");

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                for (String value : values) {
                    buffer.append(key).append("=").append(value).append("&");
                }
            }
        }

        String newUrl = buffer.toString();
        return newUrl.substring(0, newUrl.length() - 1);
    }

    private void responseJson(HttpServletResponse response, Exception exception) {

        //TODO:这里需要区分api response还是 openapi response
        Response<CustomRuntimeException> wrapper = new Response<>();
        PrintWriter writer = null;

        try {
            writer = response.getWriter();
            response.setContentType("application/json");

            // TODO:HttpMediaTypeNotAcceptableException 未找到匹配api
            if (exception instanceof BindException) {
                BindException resolveEx = (BindException) exception;
                BindingResult result = resolveEx.getBindingResult();

//                wrapper.setError(MoreObjects.firstNonNull(exception.getMessage(), messageSources.get("illegal.param.bind") + ":" + result.getFieldError().getDefaultMessage()));
                wrapper.setError(messageSources.get("illegal.param.bind") + ":" + result.getFieldError().getDefaultMessage());
                wrapper.setCode(ApiErrorCodeConstants.API_PARAM_ERROR);
                writer.write(JSON.toJSONString(wrapper));
            } else {
                // ResponseStatus responseStatus = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
                ResponseCode responseCode = AnnotationUtils.findAnnotation(exception.getClass(), ResponseCode.class);

                // 除了登录异常、以及check异常以外，其他异常改为返回模糊消息，具体异常消息不返回给用户
//               wrapper.setError(responseCode != null ? messageSources.get(responseCode.reason()) : messageSources.get("common.exception.occured") + ":" + exception.getLocalizedMessage());
                if (exception instanceof LoginException || exception instanceof IllegalArgumentException || exception instanceof IllegalStateException) {
                    wrapper.setError(responseCode != null ? messageSources.get(responseCode.reason()) : messageSources.get("common.exception.occured") + ":" + messageSources.get(exception.getLocalizedMessage()));
                } else {
                    wrapper.setError(responseCode != null ? messageSources.get(responseCode.reason()) : messageSources.get("common.exception.occured"));
                }
                wrapper.setCode(responseCode != null ? String.valueOf(responseCode.value()) : ErrorCodeConstants.INTERNAL_ERROR);
                writer.write(JSON.toJSONString(wrapper));
            }
        } catch (Exception ex) {
            // ignore
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
