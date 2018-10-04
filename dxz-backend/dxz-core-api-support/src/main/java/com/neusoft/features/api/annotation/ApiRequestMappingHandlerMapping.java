package com.neusoft.features.api.annotation;

import com.neusoft.features.api.condition.ApiVerCondition;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * 自定义HandlerMapping。
 * <p/>
 * 用于适配api版本。
 *
 * @author andy.jiao@msn.com
 */
@Slf4j
public class ApiRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Setter
    private String supportApiMinVersion = "0.1";

    @Override
    protected RequestCondition<ApiVerCondition> getCustomTypeCondition(Class<?> handlerType) {
        log.debug("[API VER] getCustomTypeCondition, type class:{}", handlerType.getName());

        ApiVer apiVer = AnnotationUtils.findAnnotation(handlerType, ApiVer.class);
        if (log.isDebugEnabled()) {
            if (apiVer == null) {
                log.debug("[API VER] getCustomTypeCondition, ApiVer not found");
            } else {
                log.debug("[API VER] getCustomTypeCondition, ApiVer.min:{} ApiVer.min:{}", apiVer.min(), apiVer.max());
            }
        }

        if (supportApiMinVersion != null && !"".equals(supportApiMinVersion)) {
            double minVersion = Double.parseDouble(supportApiMinVersion);
            if (apiVer != null && apiVer.min() >= minVersion) {
                return createCondition(apiVer);
            }
        }

        return null;
    }

    @Override
    protected RequestCondition<ApiVerCondition> getCustomMethodCondition(Method method) {
        log.debug("[API VER] getCustomMethodCondition, method name:{}", method.getName());

        ApiVer apiVer = AnnotationUtils.findAnnotation(method, ApiVer.class);
        if (log.isDebugEnabled()) {
            if (apiVer == null) {
                log.debug("[API VER] getCustomMethodCondition, ApiVer not found");
            } else {
                log.debug("[API VER] getCustomMethodCondition, ApiVer.min:{} ApiVer.min:{}", apiVer.min(), apiVer.max());
            }
        }
        return createCondition(apiVer);
    }

    private RequestCondition<ApiVerCondition> createCondition(ApiVer apiVer) {
        return apiVer == null ? null : new ApiVerCondition(apiVer.min(), apiVer.max());
    }
}

