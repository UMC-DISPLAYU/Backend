package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingLikeResponse(
    Long personalFeelingId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
