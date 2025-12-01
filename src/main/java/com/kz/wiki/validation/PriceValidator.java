package com.kz.wiki.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class PriceValidator implements ConstraintValidator<ValidPrice, BigDecimal> {
    @Override
    public void initialize(ValidPrice constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext context) {
        if (price == null) {
            return false;
        }
        // Price must be positive
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        // Price should have at most 2 decimal places
        if (price.scale() > 2) {
            return false;
        }
        return true;
    }
}


