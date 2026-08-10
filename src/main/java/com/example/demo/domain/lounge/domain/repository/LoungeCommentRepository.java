package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoungeCommentRepository {
  Optional<LoungeComment> findById(Long loungeCommentId);

  long countActiveByLoungePostId(Long loungePostId);

  Map<Long, Long> countActiveByLoungePostIds(List<Long> loungePostIds);

  Map<Long, Long> countActiveRepliesByParentCommentIds(List<Long> parentCommentIds);

  LoungeComment save(LoungeComment loungeComment);
}
