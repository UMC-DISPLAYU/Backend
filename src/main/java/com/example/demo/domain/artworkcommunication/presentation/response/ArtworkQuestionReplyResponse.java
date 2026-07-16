package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkQuestionReplyResponse(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    Long artQueId,
    Long creatorId,
    String creatorName) {}
