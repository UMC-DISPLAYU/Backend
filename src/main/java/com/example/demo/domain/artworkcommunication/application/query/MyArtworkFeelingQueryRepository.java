package com.example.demo.domain.artworkcommunication.application.query;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery.Cursor;
import java.util.List;

public interface MyArtworkFeelingQueryRepository {

  List<MyArtworkFeelingQueryItem> findByUserIdWithCursor(Long userId, Cursor cursor, int limit);
}
