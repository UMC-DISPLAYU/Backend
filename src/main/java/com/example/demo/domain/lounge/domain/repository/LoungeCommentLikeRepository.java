package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LoungeCommentLikeRepository {

  void saveIfAbsent(Long loungeCommentId, UserId userId);

  void deleteByLoungeCommentIdAndUserId(Long loungeCommentId, UserId userId);

  long countByLoungeCommentId(Long loungeCommentId);

  Map<Long, Long> countByLoungeCommentIds(List<Long> loungeCommentIds);

  Set<Long> findLikedLoungeCommentIds(List<Long> loungeCommentIds, UserId userId);
}
