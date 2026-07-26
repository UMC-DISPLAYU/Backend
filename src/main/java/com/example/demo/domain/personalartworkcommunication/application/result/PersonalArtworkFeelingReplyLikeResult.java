package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingReplyLikeResult(
    Long personalFeelingReplyId,
    Boolean liked,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
