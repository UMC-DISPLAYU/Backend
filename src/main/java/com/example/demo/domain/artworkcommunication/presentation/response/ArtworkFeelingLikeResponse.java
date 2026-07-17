package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkFeelingLikeResponse(
    Long feelingId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
