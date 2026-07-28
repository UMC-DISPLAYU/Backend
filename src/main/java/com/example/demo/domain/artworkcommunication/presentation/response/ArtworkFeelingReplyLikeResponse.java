package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkFeelingReplyLikeResponse(
    Long feelingReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
