package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.repository.LoungeCommentLikeRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungeCommentLikeRepositoryAdapter implements LoungeCommentLikeRepository {

  private final SpringDataLoungeCommentLikeJpaRepository jpaRepository;

  public JpaLoungeCommentLikeRepositoryAdapter(
      SpringDataLoungeCommentLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void saveIfAbsent(Long loungeCommentId, UserId userId) {
    jpaRepository.insertIgnore(loungeCommentId, userId.value());
  }

  @Override
  public void deleteByLoungeCommentIdAndUserId(Long loungeCommentId, UserId userId) {
    jpaRepository.deleteByLoungeCommentIdAndUserId(loungeCommentId, userId.value());
  }

  @Override
  public long countByLoungeCommentId(Long loungeCommentId) {
    return jpaRepository.countByLoungeCommentId(loungeCommentId);
  }

  @Override
  public Map<Long, Long> countByLoungeCommentIds(List<Long> loungeCommentIds) {
    return jpaRepository.countByLoungeCommentIds(loungeCommentIds).stream()
        .collect(
            Collectors.toMap(
                row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
  }

  @Override
  public Set<Long> findLikedLoungeCommentIds(List<Long> loungeCommentIds, UserId userId) {
    return Set.copyOf(jpaRepository.findLikedLoungeCommentIds(loungeCommentIds, userId.value()));
  }
}
