package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkQuestionLikeResult(
    Long questionId,
    Boolean liked,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
