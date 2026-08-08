package com.example.demo.domain.archive.presentation.response;

import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record ArchiveArtistResponse(
    @Schema(
            description = "작가 프로필 이미지 URL",
            example = "https://cdn.displayu.co.kr/artists/1/profile.png")
        String profileImageUrl,
    @Schema(description = "작가 닉네임", example = "김지원") String artistName,
    @Schema(description = "작가 활동 분야. 최대 2개", example = "[\"PAINTING\", \"ILLUSTRATION\"]")
        List<ActivityCategory> fields,
    @Schema(description = "등록 작품 수", example = "24") Long artworkCount,
    @Schema(description = "등록 전시 수", example = "8") Long exhibitionCount,
    @Schema(description = "저장 기록 ID", example = "1") Long archiveArtistId,
    @Schema(description = "작가 프로필 ID", example = "1") Long artistId,
    @Schema(description = "저장한 사용자 ID", example = "1") Long userId,
    @Schema(description = "저장 시각", example = "2026-07-13T01:49:28") LocalDateTime savedAt) {}
