package com.example.demo.domain.archive.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ArchiveDisplayToggleResponse(
    @Schema(description = "전시 ID", example = "1") Long exhibitionId,
    @Schema(description = "저장 여부 (true: 저장됨, false: 저장취소됨)", example = "true")
        boolean isArchived) {}
