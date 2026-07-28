package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkQuestionReplyResponse(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    Long questionId,
    Long creatorId,
    String creatorName) {}
