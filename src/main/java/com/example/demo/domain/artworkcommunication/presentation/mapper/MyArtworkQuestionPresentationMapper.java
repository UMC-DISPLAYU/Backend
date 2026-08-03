package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsQuery;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse.MyArtworkQuestionResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class MyArtworkQuestionPresentationMapper {

  public GetMyArtworkQuestionsQuery toQuery(Long userId, String cursor, int size) {
    return new GetMyArtworkQuestionsQuery(userId, parseCursor(cursor), size);
  }

  public MyArtworkQuestionListResponse toResponse(MyArtworkQuestionListResult result) {
    return new MyArtworkQuestionListResponse(
        result.questions().stream()
            .map(
                question ->
                    new MyArtworkQuestionResponse(
                        question.questionId(),
                        question.personalQuestionId(),
                        question.artworkId(),
                        question.personalArtworkId(),
                        question.artworkName(),
                        question.content(),
                        question.isPublic(),
                        question.answerStatus(),
                        question.createdAt()))
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
