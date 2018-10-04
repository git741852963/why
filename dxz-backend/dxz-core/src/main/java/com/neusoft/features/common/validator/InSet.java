package com.neusoft.features.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * InSet Annotation。
 *
 * @author andy.jiao@msn.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = {InSetValidator.class})
public @interface InSet {
    int[] values();

    String message() default "{illegal.param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
