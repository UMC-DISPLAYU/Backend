package com.example.demo.domain.personalartwork.presentation.response;

public record PersonalArtworkLikeResponse(
    Long personalArtworkId, boolean isLiked, long likeCount) {}
