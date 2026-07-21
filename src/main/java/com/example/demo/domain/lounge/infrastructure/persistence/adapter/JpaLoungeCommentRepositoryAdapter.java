package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeCommentRepository;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungeCommentRepositoryAdapter implements LoungeCommentRepository {

  private final SpringDataLoungeCommentJpaRepository jpaRepository;

  public JpaLoungeCommentRepositoryAdapter(SpringDataLoungeCommentJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<LoungeComment> findById(Long loungeCommentId) {
    return jpaRepository.findById(loungeCommentId);
  }

  @Override
  public List<LoungeComment> findActiveRootByCursor(Long loungePostId, Long cursorId, int limit) {
    return jpaRepository.findActiveRootByCursor(
        loungePostId, LoungeCommentStatus.ACTIVE, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public long countActiveByLoungePostId(Long loungePostId) {
    return jpaRepository.countByLoungePostIdAndStatusAndDeletedAtIsNull(
        loungePostId, LoungeCommentStatus.ACTIVE);
  }

  @Override
  public Map<Long, Long> countActiveByLoungePostIds(List<Long> loungePostIds) {
    return jpaRepository
        .countByLoungePostIdsAndStatusAndDeletedAtIsNull(loungePostIds, LoungeCommentStatus.ACTIVE)
        .stream()
        .collect(
            Collectors.toMap(
                row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
  }

  @Override
  public LoungeComment save(LoungeComment loungeComment) {
    return jpaRepository.save(loungeComment);
  }
}
