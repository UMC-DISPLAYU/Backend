package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkQuestionLikeResponse(
    Long questionId,
    Boolean liked,
    Long likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
