package com.example.demo.domain.archive.presentation.response;

import com.example.demo.domain.archive.domain.type.ArchiveDisplayStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ArchiveDisplayResponse(
    @Schema(description = "포스터 이미지 URL", example = "https://cdn.displayu.co.kr/posters/1.png")
        String posterImageUrl,
    @Schema(description = "전시 진행 상태", example = "ONGOING") ArchiveDisplayStatus status,
    @Schema(description = "전시명", example = "형태의 침묵") String title,
    @Schema(description = "주최 학교", example = "중앙대학교") String organization,
    @Schema(description = "학과", example = "디자인학부") String department,
    @Schema(description = "전시 시작일", example = "2026-05-28") LocalDate startedAt,
    @Schema(description = "전시 종료일", example = "2026-06-05") LocalDate endedAt,
    @Schema(description = "전시 장소", example = "중앙대학교 310관 갤러리") String location,
    @Schema(description = "작성한 메모 내용. 메모 없으면 null", example = "여기 조명이 인상적이었다.") String memo,
    @Schema(description = "저장 기록 ID", example = "1") Long archiveDisplayId,
    @Schema(description = "전시 ID", example = "1") Long displayId,
    @Schema(description = "저장한 사용자 ID", example = "1") Long userId,
    @Schema(description = "저장 시각", example = "2026-07-13T01:49:28") LocalDateTime savedAt) {}
