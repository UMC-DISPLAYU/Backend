package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingLikeResult(
    Long personalFeelingId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
