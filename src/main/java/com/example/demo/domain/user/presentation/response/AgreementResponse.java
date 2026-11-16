package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.type.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record AgreementResponse(
    @Schema(example = "1") Long agreementId,
    @Schema(example = "TERMS_OF_SERVICE") String code,
    @Schema(example = "서비스 이용약관") String title,
    @Schema(example = "SERVICE") Type type,
    @Schema(
            example =
                "서비스 이용약관\n시행일: 2026년 11월 21일\n제1장 총칙\n제1조 (목적)\n이 약관은 디유 운영팀(이하 \"운영자\")이 제공하는 디유(displayU) 서비스를 이용할 때 운영자와 회원 사이의 권리, 의무, 책임과 이용 절차를 정합니다.")
        String content,
    @Schema(example = "true") boolean required,
    @Schema(example = "1.0") String version,
    @Schema(example = "2026-11-21") LocalDate effectiveDate,
    @Schema(example = "1") Integer displayOrder) {}
