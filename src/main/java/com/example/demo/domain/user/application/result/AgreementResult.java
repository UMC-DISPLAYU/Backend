package com.example.demo.domain.user.application.result;

import com.example.demo.domain.user.domain.type.Type;
import java.time.LocalDate;

public record AgreementResult(
    Long agreementId,
    String code,
    String title,
    Type type,
    String content,
    boolean required,
    String version,
    LocalDate effectiveDate,
    Integer displayOrder) {}
