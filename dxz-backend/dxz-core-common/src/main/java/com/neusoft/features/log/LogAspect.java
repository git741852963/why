package com.neusoft.features.log;

import com.alibaba.fastjson.JSONObject;
import com.neusoft.features.common.dto.BaseData;
import com.neusoft.features.common.model.ApiResponse;
import com.neusoft.features.enums.OperationResultType;
import com.neusoft.features.log.annotation.OperationLog;
import com.neusoft.features.log.helpers.BasicMarker;
import com.neusoft.features.user.base.BaseUser;
import com.neusoft.features.user.base.UserUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Marker;

import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.Maps.newHashMap;

/**
 * LogInterceptor
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public class LogAspect {

    @SuppressWarnings("unused")
    private static final String LOG_CONTENT_RESULT = "RESULT";
    private static final String LOG_CONTENT_ARGS = "ARGS";
    private static final String LOG_CONTENT_TARGET = "TARGET";
    private static final String LOG_CONTENT_PROCESSING = "PROCESSING";

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String EXCEPTION = "exception";

    private HashMap<String, MethodInfo> infoCache = newHashMap();

    private ObjectMapper mapper = new ObjectMapper();

    @Setter
    private Boolean writeLogByAnnotation = false;

    private Marker coMarker = new BasicMarker("ConsoleOperation");

    public LogAspect(){
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public Object MethodCallLog(ProceedingJoinPoint point) throws Throwable {

        Object[] args = point.getArgs();

        String path = point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName();

        MethodInfo info = infoCache.get(path);

        if (info == null) {
            info = new MethodInfo();

            Class<?>[] selfParameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            Method m = point.getSignature().getDeclaringType().getMethod(point.getSignature().getName(),selfParameterTypes);

            if (m.isAnnotationPresent(OperationLog.class)) {
                OperationLog data = m.getAnnotation(OperationLog.class);
                info.setOperationName(data.value());
                if (!Strings.isNullOrEmpty(data.rewriteParam())) {
                    info.setRewrite(true);
                    info.setRewriteParam(data.rewriteParam());
                    info.setRewriteParamValue(data.rewriteParamValue());
                } else {
                    info.setRewrite(false);
                }
                info.setHasOperationLogAnnotation(true);
            } else {
                info.setOperationName(path);
                info.setRewrite(false);
                info.setHasOperationLogAnnotation(false);
            }

            if (!info.getHasOperationLogAnnotation() && writeLogByAnnotation) {
                infoCache.put(path, info);
                return point.proceed(args);
            }

            if (m.getReturnType() != null) {
                info.setHasReturnValue(true);
                if (m.getReturnType().isAssignableFrom(ApiResponse.class)) {
                    info.setIsRegularResponse(true);
                } else {
                    info.setIsRegularResponse(false);
                }
            } else {
                info.setHasReturnValue(false);
            }

            Signature signature = point.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            String[] names = methodSignature.getParameterNames();

            if (names != null && names.length > 0) {
                info.setHasParam(true);
                info.setParams(Lists.newArrayList(names));

                List<Boolean> policies = Lists.newArrayList();
                for (Class clazz : methodSignature.getParameterTypes()) {
                    if (clazz.isPrimitive()) {
                        policies.add(true);
                    } else if (BaseData.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz) ) {
                        policies.add(true);
                    } else {
                        policies.add(false);
                    }
                }

                info.setPolicies(policies);
            } else {
                info.setHasParam(false);
            }

            infoCache.put(path, info);
        }

        // 如果仅记录包含OperationLog注解的方法，直接返回
        if (!info.getHasOperationLogAnnotation() && writeLogByAnnotation) {
            return point.proceed(args);
        }

        // 获取用户信息
        BaseUser user = UserUtil.getCurrentUser();
        String userId;
        String userName;
        if (user == null) {
            userId = "";
            userName = "GUEST";
        } else {
            userId = user.getId().toString();
            userName = user.getName();
        }

        // 获取参数信息
        String argsLog = info.getHasParam() ? getArgsLog(path, args) : null;

        // 判断是否成功
        String isSuccess = "";

        // 调用目标方法并获取返回值
        Object returnValue = null;
        String returnValueLog = null;
        try {
            returnValue = point.proceed(args);
            if (info.getHasReturnValue()) {
                returnValueLog = JSONObject.toJSONString(returnValue);
            }
        } catch (Exception e) {
            StringBuffer buffer = new StringBuffer();
            returnValueLog = buffer.append("[").append(e.getClass().getName()).append("]:").append(e.getMessage()).toString();
            isSuccess = OperationResultType.EXCEPTION.value().toString();
            log.error(coMarker, "At {} 用户ID:{}, 用户名:{}, 执行:{}, 路径:{}, 是否成功:{}, 参数:{}, 结果:{}", new Date(), userId, userName, info.getOperationName(), path, isSuccess, argsLog, returnValueLog);
            Throwables.propagate(e);
        }

        if (info.getIsRegularResponse()) {
            isSuccess = JSONObject.parseObject(JSONObject.toJSONString(returnValue)).getString("success");
            if ("true".equalsIgnoreCase(isSuccess)) {
                isSuccess = OperationResultType.SUCCESS.value().toString();
            } else {
                isSuccess = OperationResultType.FAIL.value().toString();
            }
        }
        log.info(coMarker, "At {} 用户ID:{}, 用户名:{}, 执行:{}, 路径:{}, 是否成功:{}, 参数:{}, 结果:{}", new Date(), userId, userName, info.getOperationName(), path, isSuccess, argsLog, returnValueLog);

        return returnValue;
    }

    private String getArgsLog(String path, Object[] args) throws JsonProcessingException {
        MethodInfo info = infoCache.get(path);
        List<String> pNames = info.getParams();
        List<Boolean> policies = info.getPolicies();

        StringBuffer buffer = new StringBuffer();

        if (info.getRewrite()) {
            for (int i = 0; i < args.length; i++) {
                String argv;
                if (args[i] == null) {
                    argv = "null";
                } else {
                    if (pNames.get(i).equals(info.getRewriteParam())) {
                        argv = info.getRewriteParamValue();
                    } else {
                        argv = (policies.get(i) ? mapper.writeValueAsString(args[i]) : args[i].toString());
                    }
                }
                buffer.append(pNames.get(i));
                buffer.append(":");
                buffer.append(argv);
                buffer.append("\n");
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                String argv;
                if (args[i] == null) {
                    argv = "null";
                } else {
                    argv = (policies.get(i) ? mapper.writeValueAsString(args[i]) : args[i].toString());
                }
                buffer.append(pNames.get(i));
                buffer.append(":");
                buffer.append(argv);
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }

    @Data
    private class MethodInfo {
        private List<String> params;
        private List<Boolean> policies;
        private String operationName;
        private String rewriteParam;
        private String rewriteParamValue;
        private Boolean hasOperationLogAnnotation;
        private Boolean isRegularResponse;
        private Boolean hasParam;
        private Boolean hasReturnValue;
        private Boolean rewrite;
    }
}