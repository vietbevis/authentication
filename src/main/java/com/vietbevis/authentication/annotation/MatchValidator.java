package com.vietbevis.authentication.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class MatchValidator implements ConstraintValidator<MatchFields, Object> {

    private String passwordField;
    private String confirmPasswordField;
    private String fieldError;
    private String message;

    @Override
    public void initialize(MatchFields constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
        this.fieldError = constraintAnnotation.fieldError();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);

        Object passwordValue = beanWrapper.getPropertyValue(passwordField);
        Object confirmPasswordValue = beanWrapper.getPropertyValue(confirmPasswordField);

        boolean isValid = false;

        if (passwordValue == null && confirmPasswordValue == null) {
            isValid = true;
        } else if (passwordValue != null) {
            isValid = passwordValue.equals(confirmPasswordValue);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldError)
                .addConstraintViolation();
        }

        return isValid;
    }
}