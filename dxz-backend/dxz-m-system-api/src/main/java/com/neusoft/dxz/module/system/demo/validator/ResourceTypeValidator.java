package com.neusoft.dxz.module.system.demo.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import com.neusoft.dxz.module.system.demo.enums.ResourceType;

/**
 * 资源类型验证。
 *
 * 尝试使用baidu fluent validator，发现并不好用，暂时保留...
 *
 * @author andy.jiao@msn.com
 */
@Deprecated
public class ResourceTypeValidator<T> extends ValidatorHandler<Integer> implements Validator<Integer> {

    @Override
    public boolean validate(ValidatorContext context, Integer t) {
        if (ResourceType.from(t) != null) {
            context.addErrorMsg("system.resource.param.type.error");
            return false;
        }
        return true;
    }
}
