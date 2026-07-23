package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkQuestionReplyResponse(
    Long queReplyId,
    String content,
    LocalDateTime createdAt,
    Long artQueId,
    Long creatorId,
    String creatorName) {}
