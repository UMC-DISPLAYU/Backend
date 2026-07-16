package com.example.demo.domain.archive.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ArchiveDisplayResponse(
    @Schema(description = "저장 기록 ID", example = "1") Long archiveDisplayId,
    @Schema(description = "전시(Display) ID", example = "1") Long displayId,
    @Schema(description = "저장한 사용자 ID", example = "1") Long userId,
    @Schema(description = "저장 시각", example = "2026-07-13T01:49:28") LocalDateTime savedAt) {}
