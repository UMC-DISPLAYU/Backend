package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkFeelingReplyLikeResult(
    Long feelingReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
