package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record MyArtworkQuestionQueryItem(
    Long itemId,
    int sourceOrder,
    Long questionId,
    Long personalQuestionId,
    Long artworkId,
    Long personalArtworkId,
    String artworkName,
    String content,
    Boolean isPublic,
    AnswerStatus answerStatus,
    LocalDateTime createdAt) {}
