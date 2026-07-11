package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoungeCommentRepository {
  Optional<LoungeComment> findById(Long loungeCommentId);

  List<LoungeComment> findActiveByLoungePostId(Long loungePostId);

  List<LoungeComment> findActiveRootByCursor(Long loungePostId, Long cursorId, int limit);

  List<LoungeComment> findActiveRepliesByCursor(Long parentCommentId, Long cursorId, int limit);

  long countActiveByLoungePostId(Long loungePostId);

  Map<Long, Long> countActiveByLoungePostIds(List<Long> loungePostIds);

  Map<Long, Long> countActiveRepliesByParentCommentIds(List<Long> parentCommentIds);

  LoungeComment save(LoungeComment loungeComment);

  void delete(LoungeComment loungeComment);
}
