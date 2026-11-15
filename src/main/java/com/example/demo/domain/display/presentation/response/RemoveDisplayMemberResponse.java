package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RemoveDisplayMemberResponse(
    @Schema(example = "1") Long displayId,
    @Schema(example = "12") Long removedMemberId,
    @Schema(example = "103") Long removedUserId,
    @Schema(example = "2") int deletedArtworkCount) {}
