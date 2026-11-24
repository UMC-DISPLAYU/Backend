package com.example.demo.domain.admin.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DeletionReasonValidator implements ConstraintValidator<ValidDeletionReason, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return false;
    int normalizedLength = value.strip().length();
    return normalizedLength >= 1 && normalizedLength <= 1000;
  }
}
