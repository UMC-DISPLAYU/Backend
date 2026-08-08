package com.example.demo.domain.displayartwork.application.query;

public record ArtworkSummaryQueryResult(
    Long displayArtworkId, String artworkName, String artistName, String artworkImageUrl) {}
