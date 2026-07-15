package com.example.demo.domain.user.application.mapper;

import com.example.demo.domain.user.application.result.SchoolEmailConfirmVerificationResult;
import com.example.demo.domain.user.presentation.response.SchoolEmailVerificationConfirmResponse;
import org.springframework.stereotype.Component;

@Component
public class SchoolEmailVerificationMapper {

  public SchoolEmailVerificationConfirmResponse toResponse(
      SchoolEmailConfirmVerificationResult result) {
    return new SchoolEmailVerificationConfirmResponse(result.schoolEmail(), result.isVerified());
  }
}
