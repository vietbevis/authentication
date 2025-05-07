package com.vietbevis.authentication.annotation;

import com.vietbevis.authentication.validator.MatchValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchValidator.class)
@Documented
public @interface MatchFields {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String primaryField();

    String confirmationField();

    String errorFieldName();
} 