package com.example.demo.domain.personalartwork.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record PersonalArtworkSummaryResponse(
    Long personalArtworkId,
    String artworkName,
    String thumbnailUrl,
    String type,
    List<String> types,
    LocalDateTime createdAt) {}
