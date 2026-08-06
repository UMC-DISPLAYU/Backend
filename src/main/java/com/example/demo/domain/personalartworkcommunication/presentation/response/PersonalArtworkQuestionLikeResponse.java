package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkQuestionLikeResponse(
    Long personalQuestionId,
    Boolean liked,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
