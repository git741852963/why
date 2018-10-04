package com.neusoft.features.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;

/**
 * ValidEnum Validator。
 *
 * @author andy.jiao@msn.com
 */
public class ValidEnumValidator implements ConstraintValidator<ValidEnum, Object> {
    private Class clazz;

    @Override
    public void initialize(ValidEnum inEnum) {
        this.clazz = inEnum.clazz();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        if (clazz.isAssignableFrom(ValuedEnum.class)) {
            //TODO:如果接口没有实现ValuedEnum接口，直接返回，这里应该抛异常
            return true;
        }

        EnumSet set = EnumSet.allOf(clazz);

        if (obj != null) {
            for(Object o : set) {
                Object val = ((ValuedEnum)o).value();
                if (obj.equals(val)) {
                    return true;
                }
            }
        } else {
            return true;
        }

        return false;
    }
}
