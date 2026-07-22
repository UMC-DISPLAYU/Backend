package com.example.demo.domain.personalartworkcommunication.presentation.response;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record PersonalArtworkQuestionResponse(
    Long personalQuestionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long userId) {}
