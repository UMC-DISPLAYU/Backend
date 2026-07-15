package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;

public record SchoolEmailVerificationConfirmResponse(String schoolEmail, boolean isVerified) {

  public static SchoolEmailVerificationConfirmResponse from(SchoolEmailVerification verification) {
    return new SchoolEmailVerificationConfirmResponse(
        verification.getSchoolEmail(), verification.isVerified());
  }
}
