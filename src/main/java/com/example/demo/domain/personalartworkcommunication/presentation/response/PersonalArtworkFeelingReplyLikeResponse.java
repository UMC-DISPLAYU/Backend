package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingReplyLikeResponse(
    Long personalFeelingReplyId,
    Boolean liked,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
