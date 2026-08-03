package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec;
import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec.DecodedCursor;
import com.example.demo.domain.artworkcommunication.application.query.GetReceivedArtworkQuestionsQuery;
import com.example.demo.domain.artworkcommunication.application.query.GetReceivedArtworkQuestionsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.result.ReceivedArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import com.example.demo.domain.artworkcommunication.presentation.response.ReceivedArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ReceivedArtworkQuestionListResponse.ReceivedArtworkQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class ReceivedArtworkQuestionPresentationMapper {

  public GetReceivedArtworkQuestionsQuery toQuery(
      Long userId, AnswerStatus answerStatus, String cursor, int size) {
    return new GetReceivedArtworkQuestionsQuery(userId, answerStatus, parseCursor(cursor), size);
  }

  public ReceivedArtworkQuestionListResponse toResponse(ReceivedArtworkQuestionListResult result) {
    return new ReceivedArtworkQuestionListResponse(
        result.questions().stream()
            .map(
                question ->
                    new ReceivedArtworkQuestionResponse(
                        question.questionId(),
                        question.personalQuestionId(),
                        question.artworkId(),
                        question.personalArtworkId(),
                        question.artworkName(),
                        question.content(),
                        question.isPublic(),
                        question.answerStatus(),
                        question.questionerId(),
                        question.questionerNickname(),
                        question.createdAt()))
            .toList(),
        result.nextCursor(),
        result.size(),
        result.hasNext());
  }

  private Cursor parseCursor(String cursor) {
    DecodedCursor decoded = ArtworkCursorCodec.decode(cursor);
    return decoded == null
        ? null
        : new Cursor(decoded.createdAt(), decoded.sourceOrder(), decoded.itemId());
  }
}
