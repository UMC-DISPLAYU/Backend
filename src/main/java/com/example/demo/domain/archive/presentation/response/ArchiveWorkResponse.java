package com.example.demo.domain.archive.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ArchiveWorkResponse(
    @Schema(
            description = "작품 대표 이미지 URL",
            example = "https://cdn.displayu.co.kr/artworks/12/thumb.jpg")
        String artworkImageUrl,
    @Schema(description = "작품명", example = "FORM 2026") String artworkName,
    @Schema(description = "이 작품이 등록된 전시에서의 작가명", example = "고상준") String artistName,
    @Schema(description = "작성한 메모 내용. 메모 없으면 null", example = "이 작품의 색감이 좋았다.") String memo,
    @Schema(description = "저장 기록 ID", example = "1") Long archiveWorkId,
    @Schema(description = "전시 작품 ID. 개인 작품 저장 기록이면 null", example = "1") Long artworkId,
    @Schema(description = "개인 작품 ID. 전시 작품 저장 기록이면 null", example = "null") Long personalArtworkId,
    @Schema(description = "저장한 사용자 ID", example = "1") Long userId,
    @Schema(description = "저장 시각", example = "2026-07-13T01:49:28") LocalDateTime savedAt) {}
