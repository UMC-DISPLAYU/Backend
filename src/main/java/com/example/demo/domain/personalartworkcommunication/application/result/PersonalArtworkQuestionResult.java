package com.example.demo.domain.personalartworkcommunication.application.result;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record PersonalArtworkQuestionResult(
    Long personalQuestionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long userId) {}
