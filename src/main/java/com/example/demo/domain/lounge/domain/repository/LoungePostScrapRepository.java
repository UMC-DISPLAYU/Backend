package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LoungePostScrapRepository {

  void saveIfAbsent(Long loungePostId, UserId userId);

  void deleteByLoungePostIdAndUserId(Long loungePostId, UserId userId);

  long countByLoungePostId(Long loungePostId);

  Map<Long, Long> countByLoungePostIds(List<Long> loungePostIds);

  Set<Long> findScrappedLoungePostIds(List<Long> loungePostIds, UserId userId);
}
