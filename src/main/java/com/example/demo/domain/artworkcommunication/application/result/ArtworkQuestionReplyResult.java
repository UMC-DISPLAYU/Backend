package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkQuestionReplyResult(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    Long artQueId,
    Long creatorId,
    String creatorName) {}
