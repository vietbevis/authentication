package com.vietbevis.authentication.annotation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private List<String> acceptedValues;
    private boolean required;
 
    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toList());
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return !required;
        }

        boolean valid = acceptedValues.contains(value);
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Giá trị '" + value + "' không hợp lệ. Các giá trị hợp lệ là: " + String.join(", ",
                        acceptedValues))
                .addConstraintViolation();
        }
        return valid;
    }
}