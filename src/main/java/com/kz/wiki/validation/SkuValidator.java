package com.kz.wiki.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SkuValidator implements ConstraintValidator<ValidSku, String> {
    @Override
    public void initialize(ValidSku constraintAnnotation) {
    }

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        if (sku == null || sku.trim().isEmpty()) {
            return false;
        }
        // SKU should be 3-50 characters, alphanumeric with dashes and underscores
        return sku.matches("^[A-Za-z0-9_-]{3,50}$");
    }
}







