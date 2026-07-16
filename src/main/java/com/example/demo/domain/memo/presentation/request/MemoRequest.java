package com.example.demo.domain.memo.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MemoRequest(
    @Schema(description = "메모 내용 (최대 100자)", example = "여기 조명이 인상적이었다.") @NotBlank @Size(max = 100) String content,
    @Schema(description = "방문일", example = "2026-07-16") LocalDate visitDate) {}
