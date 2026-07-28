package com.example.demo.domain.artworkcommunication.application.result;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record ArtworkQuestionResult(
    Long questionId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    Long displayArtworkId,
    Long userId) {}
