package com.example.demo.domain.lounge.application.query;

import java.util.List;
import java.util.Map;

public interface LoungePostQueryRepository {

  List<LoungePostQueryResult> findActiveByAuthorCursor(Long authorUserId, Long cursorId, int limit);

  List<LoungePostQueryResult> findActiveScrappedByUserCursor(Long userId, Long cursorId, int limit);

  List<LoungePostQueryResult> findActiveCommentedByUserCursor(
      Long userId, Long cursorId, int limit);

  Map<Long, List<String>> findImageUrlsByLoungePostIds(List<Long> loungePostIds);
}
