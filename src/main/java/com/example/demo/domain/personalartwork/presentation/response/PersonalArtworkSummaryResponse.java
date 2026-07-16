package com.example.demo.domain.personalartwork.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkSummaryResponse(
    Long personalArtworkId,
    String artworkName,
    String thumbnailUrl,
    String type,
    LocalDateTime createdAt) {}
