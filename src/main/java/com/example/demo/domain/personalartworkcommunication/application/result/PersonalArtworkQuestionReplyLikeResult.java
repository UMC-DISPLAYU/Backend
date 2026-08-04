package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkQuestionReplyLikeResult(
    Long personalQuestionReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
