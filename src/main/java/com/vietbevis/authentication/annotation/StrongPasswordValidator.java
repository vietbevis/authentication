package com.vietbevis.authentication.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private int minLength;
    private int maxLength;
    private int minUppercase;
    private int minLowercase;
    private int minNumber;
    private int minSpecial;
    private String specialChars;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.minUppercase = constraintAnnotation.minUppercase();
        this.minLowercase = constraintAnnotation.minLowercase();
        this.minNumber = constraintAnnotation.minNumber();
        this.minSpecial = constraintAnnotation.minSpecial();
        this.specialChars = constraintAnnotation.specialChars();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        if (password.length() < minLength || password.length() > maxLength) {
            return false;
        }

        long uppercaseCount = password.chars()
                .filter(Character::isUpperCase)
                .count();
        if (uppercaseCount < minUppercase) {
            return false;
        }

        long lowercaseCount = password.chars()
                .filter(Character::isLowerCase)
                .count();
        if (lowercaseCount < minLowercase) {
            return false;
        }

        long numberCount = password.chars()
                .filter(Character::isDigit)
                .count();
        if (numberCount < minNumber) {
            return false;
        }

        long specialCount = password.chars()
                .filter(ch -> specialChars.indexOf(ch) != -1)
                .count();
        if (specialCount < minSpecial) {
            return false;
        }

        return true;
    }
} 