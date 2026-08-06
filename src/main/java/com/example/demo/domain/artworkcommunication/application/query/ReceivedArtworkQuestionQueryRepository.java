package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.query.GetReceivedArtworkQuestionsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import java.util.List;

public interface ReceivedArtworkQuestionQueryRepository {

  List<ReceivedArtworkQuestionQueryItem> findReceivedByUserIdAndAnswerStatusWithCursor(
      Long userId, AnswerStatus answerStatus, Cursor cursor, int limit);
}
