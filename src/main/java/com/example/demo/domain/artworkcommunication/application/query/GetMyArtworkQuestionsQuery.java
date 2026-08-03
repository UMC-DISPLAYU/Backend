package com.example.demo.domain.artworkcommunication.application.query;

import java.time.LocalDateTime;

public record GetMyArtworkQuestionsQuery(Long userId, Cursor cursor, int size) {

  public record Cursor(LocalDateTime createdAt, int sourceOrder, Long itemId) {}
}
