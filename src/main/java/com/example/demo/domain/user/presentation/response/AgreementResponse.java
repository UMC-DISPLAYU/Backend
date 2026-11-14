package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.type.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record AgreementResponse(
    @Schema(example = "1") Long agreementId,
    @Schema(example = "TERMS_OF_SERVICE") String code,
    @Schema(example = "서비스 이용약관") String title,
    @Schema(example = "SERVICE") Type type,
    @Schema(example = "DisplayU 서비스 이용약관 내용입니다.") String content,
    @Schema(example = "true") boolean required,
    @Schema(example = "1.0") String version,
    @Schema(example = "2026-01-01") LocalDate effectiveDate,
    @Schema(example = "1") Integer displayOrder) {}
