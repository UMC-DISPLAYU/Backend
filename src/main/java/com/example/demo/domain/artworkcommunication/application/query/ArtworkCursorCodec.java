package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;

public final class ArtworkCursorCodec {

  private ArtworkCursorCodec() {}

  public static String encode(LocalDateTime createdAt, int sourceOrder, Long itemId) {
    String value = createdAt + "|" + sourceOrder + "|" + itemId;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  public static DecodedCursor decode(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    try {
      String decoded =
          new String(Base64.getUrlDecoder().decode(cursor.trim()), StandardCharsets.UTF_8);
      String[] parts = decoded.split("\\|", -1);
      if (parts.length != 3) {
        throw new IllegalArgumentException("cursor must contain three parts");
      }

      LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
      int sourceOrder = Integer.parseInt(parts[1]);
      Long itemId = Long.valueOf(parts[2]);
      if ((sourceOrder != 0 && sourceOrder != 1) || itemId < 1) {
        throw new IllegalArgumentException("cursor values are out of range");
      }
      return new DecodedCursor(createdAt, sourceOrder, itemId);
    } catch (IllegalArgumentException | DateTimeParseException exception) {
      BusinessException businessException =
          new BusinessException(GlobalErrorCode.INVALID_REQUEST, "유효하지 않은 cursor입니다.");
      businessException.initCause(exception);
      throw businessException;
    }
  }

  public record DecodedCursor(LocalDateTime createdAt, int sourceOrder, Long itemId) {}
}
