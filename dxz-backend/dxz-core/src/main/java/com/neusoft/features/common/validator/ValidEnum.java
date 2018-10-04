package com.neusoft.features.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * ValidEnum Annotation。
 *
 * @author andy.jiao@msn.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = {ValidEnumValidator.class})
public @interface ValidEnum {
    Class<?> clazz();

    String message() default "{illegal.param}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
