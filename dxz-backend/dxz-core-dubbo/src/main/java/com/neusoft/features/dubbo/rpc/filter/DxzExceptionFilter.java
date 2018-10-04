package com.neusoft.features.dubbo.rpc.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.neusoft.features.common.model.Response;
import com.neusoft.features.dubbo.annotation.MethodInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Service全局错误处理。
 *
 * @author andy.jiao@msn.com
 */
@Activate(group = Constants.PROVIDER)
@Slf4j
public class DxzExceptionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException() && GenericService.class != invoker.getInterface()) {

                try {
                    Throwable exception = result.getException();

                    try {
                        Method method = invoker.getInterface().getMethod(invocation.getMethodName(),
                                                                         invocation.getParameterTypes());

                        Annotation[] annotations = method.getDeclaredAnnotations();
                        MethodInfo info = null;
                        for (Annotation annotation : annotations) {
                            if (annotation.annotationType() == MethodInfo.class) {
                                info = (MethodInfo) annotation;
                                break;
                            }
                        }

                        if (info != null) {
                            // 找到Method Info
                            List<Object> list = new ArrayList();
                            for (Object o : invocation.getArguments()) {
                                list.add(o);
                            }
                            list.add(exception.getMessage());
                            log.debug(info.failLog(), list.toArray());
                        }

                        Class rtnClazz = method.getReturnType();
                        Response resp = (Response) rtnClazz.newInstance();
                        resp.setError(exception.getMessage());

                        return new RpcResult(resp);
                    } catch (NoSuchMethodException e) {
                        return result;
                    }

                } catch (Throwable e) {
                    log.warn("error occured when called by " + RpcContext.getContext().getRemoteHost() +
                                     ". service: " + invoker.getInterface().getName() + ", method: " +
                                     invocation.getMethodName() + ", exception: " + e.getClass().getName() +
                                     ": " + e.getMessage(), e);
                    return result;
                }
            }
            return result;
        } catch (RuntimeException e) {
            log.error("Got unchecked and undeclared exception which called by " +
                              RpcContext.getContext().getRemoteHost() + ". service: " +
                              invoker.getInterface().getName() + ", method: " +
                              invocation.getMethodName() + ", exception: " +
                              e.getClass().getName() + ": " + e.getMessage(), e);
            throw e;
        }
    }
}
