package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.application.query.LoungeCommentQueryRepository;
import com.example.demo.domain.lounge.application.query.LoungeCommentQueryResult;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentQueryJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungeCommentQueryRepositoryAdapter implements LoungeCommentQueryRepository {

  private final SpringDataLoungeCommentQueryJpaRepository jpaRepository;

  public JpaLoungeCommentQueryRepositoryAdapter(
      SpringDataLoungeCommentQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LoungeCommentQueryResult> findActiveById(Long loungeCommentId) {
    return jpaRepository.findActiveById(loungeCommentId, LoungeCommentStatus.ACTIVE);
  }

  @Override
  public List<LoungeCommentQueryResult> findActiveRootByCursor(
      Long loungePostId, Long cursorId, int limit) {
    return jpaRepository.findActiveRootByCursor(
        loungePostId, LoungeCommentStatus.ACTIVE, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public List<LoungeCommentQueryResult> findActiveRepliesByParentCommentIds(
      List<Long> parentCommentIds) {
    return jpaRepository.findActiveRepliesByParentCommentIds(
        parentCommentIds, LoungeCommentStatus.ACTIVE);
  }
}
