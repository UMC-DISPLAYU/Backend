package com.example.demo.domain.archive.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ArchiveDisplayCursorResponse(
    List<ArchiveDisplayResponse> displays,
    @Schema(description = "다음 페이지 조회용 커서 (archiveDisplayId). 마지막 페이지면 null", example = "1")
        Long nextCursorId,
    @Schema(description = "페이지 크기", example = "10") int size,
    @Schema(description = "다음 페이지 존재 여부", example = "false") boolean hasNext) {}
