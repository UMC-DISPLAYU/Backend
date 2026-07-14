package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery.Cursor;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record ClosingSoonDisplayRequest(
    @Schema(description = "마지막으로 조회한 전시의 endedAt과 displayId를 ':'로 연결한 커서", example = "2026-07-15:5")
        String cursor,
    @Min(1) @Max(100) Integer size) {

  private static final int DEFAULT_SIZE = 20;
  private static final String CURSOR_SEPARATOR = ":";

  public ClosingSoonDisplayQuery toQuery() {
    return new ClosingSoonDisplayQuery(parseCursor(), size == null ? DEFAULT_SIZE : size);
  }

  private Cursor parseCursor() {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    String[] cursorParts = cursor.trim().split(CURSOR_SEPARATOR, -1);
    if (cursorParts.length != 2) {
      throw invalidCursorException();
    }

    try {
      LocalDate endedAt = LocalDate.parse(cursorParts[0]);
      Long displayId = Long.valueOf(cursorParts[1]);
      if (displayId < 1) {
        throw invalidCursorException();
      }
      return new Cursor(endedAt, displayId);
    } catch (DateTimeParseException | NumberFormatException exception) {
      throw invalidCursorException();
    }
  }

  private BusinessException invalidCursorException() {
    return new BusinessException(
        GlobalErrorCode.INVALID_REQUEST, "cursor는 yyyy-MM-dd:displayId 형식이어야 합니다.");
  }
}
