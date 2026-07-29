package com.example.demo.domain.user.presentation.mapper;

import com.example.demo.domain.user.application.result.AgreementResult;
import com.example.demo.domain.user.presentation.response.AgreementResponse;
import org.springframework.stereotype.Component;

@Component
public class AgreementPresentationMapper {

  public AgreementResponse toResponse(AgreementResult result) {
    return new AgreementResponse(
        result.agreementId(),
        result.code(),
        result.title(),
        result.type(),
        result.content(),
        result.required(),
        result.version(),
        result.effectiveDate(),
        result.displayOrder());
  }
}
