package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkFeelingLikeResult(
    Long feelingId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
