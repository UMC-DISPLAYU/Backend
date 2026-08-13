package com.example.demo.domain.memo.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemoResponse(
    @Schema(description = "메모 ID", example = "1") Long memoId,
    @Schema(description = "저장된 전시(ArchiveDisplay) ID. 전시 메모가 아니면 null", example = "1")
        Long archiveDisplayId,
    @Schema(description = "저장된 작품(ArchiveWork) ID. 작품 메모가 아니면 null", example = "null")
        Long archiveWorkId,
    @Schema(description = "저장된 개인 작품(ArchivePersonalWork) ID. 개인 작품 메모가 아니면 null", example = "null")
        Long archivePersonalWorkId,
    @Schema(description = "메모 내용", example = "여기 조명이 인상적이었다.") String content,
    @Schema(description = "방문일", example = "2026-07-16") LocalDate visitDate,
    @Schema(description = "작성 시각", example = "2026-07-16T10:00:00") LocalDateTime createdAt,
    @Schema(description = "수정 시각", example = "2026-07-16T10:00:00") LocalDateTime updatedAt) {}
