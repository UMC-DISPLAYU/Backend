package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record ReceivedArtworkQuestionQueryItem(
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
    Long questionerId,
    String questionerNickname,
    LocalDateTime createdAt) {}
