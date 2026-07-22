package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkQuestionReplyResult(
    Long personalQuestionReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalQuestionId,
    Long userId,
    String nickname,
    Boolean isCreator) {}
