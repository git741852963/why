package com.neusoft.features.common.validator;

import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * InSet Validator。
 *
 * @author andy.jiao@msn.com
 */
public class InSetValidator implements ConstraintValidator<InSet, Integer> {
    private Set<Integer> values;

    @Override
    public void initialize(InSet inSet) {
        values = Sets.newHashSet();
        for (Integer val : inSet.values()) {
            values.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return values.contains(value);
    }
}
