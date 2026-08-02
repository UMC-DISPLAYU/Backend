package com.example.demo.domain.lounge.application.query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoungeCommentQueryRepository {
  Optional<LoungeCommentQueryResult> findActiveById(Long loungeCommentId);

  List<LoungeCommentQueryResult> findActiveRootByCursor(
      Long loungePostId, Long cursorId, int limit);

  List<LoungeCommentQueryResult> findActiveRepliesByCursor(
      Long parentCommentId, Long cursorId, int limit);

  Map<Long, List<String>> findImageUrlsByLoungeCommentIds(List<Long> loungeCommentIds);
}
