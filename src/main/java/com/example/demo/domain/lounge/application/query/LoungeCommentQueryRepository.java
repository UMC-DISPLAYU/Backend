package com.example.demo.domain.lounge.application.query;

import java.util.List;

public interface LoungeCommentQueryRepository {
  List<LoungeCommentQueryResult> findActiveRootByCursor(
      Long loungePostId, Long cursorId, int limit);

  List<LoungeCommentQueryResult> findActiveRepliesByCursor(
      Long parentCommentId, Long cursorId, int limit);
}
