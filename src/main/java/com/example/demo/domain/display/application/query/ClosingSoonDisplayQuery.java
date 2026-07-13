package com.example.demo.domain.display.application.query;

import java.time.LocalDate;

public record ClosingSoonDisplayQuery(Cursor cursor, int size) {

  public record Cursor(LocalDate endedAt, Long displayId) {

    public String value() {
      return endedAt + ":" + displayId;
    }
  }
}
