package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec;
import com.example.demo.domain.artworkcommunication.application.query.ArtworkCursorCodec.DecodedCursor;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsQuery;
import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse.MyArtworkQuestionResponse;
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
    DecodedCursor decoded = ArtworkCursorCodec.decode(cursor);
    return decoded == null
        ? null
        : new Cursor(decoded.createdAt(), decoded.sourceOrder(), decoded.itemId());
  }
}
