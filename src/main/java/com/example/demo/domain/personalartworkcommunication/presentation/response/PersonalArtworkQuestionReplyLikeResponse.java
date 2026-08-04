package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkQuestionReplyLikeResponse(
    Long personalQuestionReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
