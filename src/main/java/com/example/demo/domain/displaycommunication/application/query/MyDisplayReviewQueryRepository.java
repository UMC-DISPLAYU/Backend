package com.example.demo.domain.displaycommunication.application.query;

import java.util.List;

public interface MyDisplayReviewQueryRepository {

  List<MyDisplayReviewQueryItem> findByUserIdWithCursor(Long userId, Long cursorId, int limit);
}
