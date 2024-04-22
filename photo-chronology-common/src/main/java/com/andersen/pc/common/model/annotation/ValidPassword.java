package com.andersen.pc.common.model.annotation;

import com.andersen.pc.common.model.annotation.validation.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.andersen.pc.common.constant.Constant.Errors.PASSWORD_VALIDATION_FAILED;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String locale() default "";
    String message() default PASSWORD_VALIDATION_FAILED;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
