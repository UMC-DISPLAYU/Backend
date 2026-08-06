package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsQuery.Cursor;
import java.util.List;

public interface MyArtworkQuestionQueryRepository {

  List<MyArtworkQuestionQueryItem> findByUserIdWithCursor(Long userId, Cursor cursor, int limit);
}
