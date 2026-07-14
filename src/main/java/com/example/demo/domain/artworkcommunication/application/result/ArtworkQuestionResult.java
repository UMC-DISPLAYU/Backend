package com.example.demo.domain.artworkcommunication.application.result;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record ArtworkQuestionResult(
    Long artQueId,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt,
    Long displayArtworkId,
    Long userId) {}
