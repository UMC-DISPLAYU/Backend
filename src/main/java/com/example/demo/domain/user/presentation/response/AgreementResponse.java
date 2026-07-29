package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.enums.Type;
import java.time.LocalDate;

public record AgreementResponse(
    Long agreementId,
    String code,
    String title,
    Type type,
    String content,
    boolean required,
    String version,
    LocalDate effectiveDate,
    Integer displayOrder) {}
