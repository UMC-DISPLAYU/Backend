package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse.MyArtworkFeelingResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class MyArtworkFeelingPresentationMapper {

  public GetMyArtworkFeelingsQuery toQuery(Long userId, String cursor, int size) {
    return new GetMyArtworkFeelingsQuery(userId, parseCursor(cursor), size);
  }

  public MyArtworkFeelingListResponse toResponse(MyArtworkFeelingListResult result) {
    return new MyArtworkFeelingListResponse(
        result.feelings().stream()
            .map(
                feeling ->
                    new MyArtworkFeelingResponse(
                        feeling.artworkId(),
                        feeling.personalArtworkId(),
                        feeling.artworkName(),
                        feeling.content(),
                        feeling.createdAt()))
            .toList(),
        result.nextCursor(),
        result.size(),
        result.hasNext());
  }

  private Cursor parseCursor(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    try {
      String decoded =
          new String(Base64.getUrlDecoder().decode(cursor.trim()), StandardCharsets.UTF_8);
      String[] parts = decoded.split("\\|", -1);
      if (parts.length != 3) {
        throw invalidCursorException();
      }
      LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
      int sourceOrder = Integer.parseInt(parts[1]);
      Long itemId = Long.valueOf(parts[2]);
      if ((sourceOrder != 0 && sourceOrder != 1) || itemId < 1) {
        throw invalidCursorException();
      }
      return new Cursor(createdAt, sourceOrder, itemId);
    } catch (IllegalArgumentException | DateTimeParseException exception) {
      throw invalidCursorException();
    }
  }

  private BusinessException invalidCursorException() {
    return new BusinessException(GlobalErrorCode.INVALID_REQUEST, "유효하지 않은 cursor입니다.");
  }
}
