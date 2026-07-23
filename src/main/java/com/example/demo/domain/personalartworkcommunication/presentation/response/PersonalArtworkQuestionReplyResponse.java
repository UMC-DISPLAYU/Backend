package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkQuestionReplyResponse(
    Long personalQuestionReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalQuestionId,
    Long userId,
    String nickname,
    Boolean isCreator) {}
