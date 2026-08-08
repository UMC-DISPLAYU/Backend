package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import java.util.List;
import java.util.Map;

public interface LoungePostQueryRepository {

  List<LoungePostQueryResult> findActiveByAuthorCursor(
      Long authorUserId, List<LoungePostCategory> categories, Long cursorId, int limit);

  List<LoungePostQueryResult> findActiveScrappedByUserCursor(
      Long userId, List<LoungePostCategory> categories, Long cursorId, int limit);

  List<LoungePostQueryResult> findActiveCommentedByUserCursor(
      Long userId, List<LoungePostCategory> categories, Long cursorId, int limit);

  Map<Long, List<String>> findImageUrlsByLoungePostIds(List<Long> loungePostIds);
}
