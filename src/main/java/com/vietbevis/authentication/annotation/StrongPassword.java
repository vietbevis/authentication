package com.vietbevis.authentication.annotation;

import com.vietbevis.authentication.validator.StrongPasswordValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {

    String message() default "Mật khẩu không đáp ứng yêu cầu về độ mạnh";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int minLength() default 8;

    int maxLength() default 50;

    int minUppercase() default 1;

    int minLowercase() default 1;

    int minNumber() default 1;

    int minSpecial() default 1;

    String specialChars() default "!@#$%^&*()_+-=[]{};':\"\\|,.<>/?";
} 