package com.example.demo.domain.artworkcommunication.presentation.response;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record ArtworkQuestionResponse(
    Long questionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long displayArtworkId,
    Long userId) {}
