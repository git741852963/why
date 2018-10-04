package com.neusoft.features.common.service;

import com.baidu.unbiz.fluentvalidator.FluentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * BaseService
 *
 * @author andy.jiao@msn.com
 * @date Apr 16, 2015
 */
public class BaseService {

    /**
     * log
     */
    protected Logger log = null;

    /**
     * bean验证 jsr303
     */
    @Autowired
    private Validator validator;

    /**
     * 构造函数，创建log对象
     */
    public BaseService() {
        log = LoggerFactory.getLogger(getClass().getName());
    }

    /**
     * 使用jsr303进行bean验证
     *
     * @param t     泛型t
     * @param group 验证分组
     * @throws IllegalArgumentException 验证失败抛出
     */
    protected <T> void validate(T t, Class<?> group) throws IllegalArgumentException {
        if (t == null) {
            throw new IllegalArgumentException("illegal.param.null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(t, group);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<?> violation : violations) {
                throw new IllegalArgumentException(violation.getMessage());
            }
        }
    }

    /**
     * 获取FluentValidator
     *
     * @return FluentValidator
     */
    protected FluentValidator getValidator() {
        return FluentValidator.checkAll();
    }
}
