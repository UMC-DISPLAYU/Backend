package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;

public record GetReceivedArtworkQuestionsQuery(
    Long userId, AnswerStatus answerStatus, Cursor cursor, int size) {

  public record Cursor(LocalDateTime createdAt, int sourceOrder, Long itemId) {}
}
