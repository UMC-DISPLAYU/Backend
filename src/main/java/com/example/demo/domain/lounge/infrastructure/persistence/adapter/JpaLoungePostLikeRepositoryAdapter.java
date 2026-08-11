package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.domain.repository.LoungePostLikeRepository;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungePostLikeRepositoryAdapter implements LoungePostLikeRepository {

  private final SpringDataLoungePostLikeJpaRepository jpaRepository;

  public JpaLoungePostLikeRepositoryAdapter(SpringDataLoungePostLikeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void saveIfAbsent(Long loungePostId, UserId userId) {
    jpaRepository.insertIfAbsent(loungePostId, userId.value());
  }

  @Override
  public int deleteByLoungePostIdAndUserId(Long loungePostId, UserId userId) {
    return jpaRepository.deleteByLoungePostIdAndUserId(loungePostId, userId.value());
  }

  @Override
  public long countByLoungePostId(Long loungePostId) {
    return jpaRepository.countByLoungePostId(loungePostId);
  }

  @Override
  public Map<Long, Long> countByLoungePostIds(List<Long> loungePostIds) {
    return jpaRepository.countByLoungePostIds(loungePostIds).stream()
        .collect(
            Collectors.toMap(
                row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
  }

  @Override
  public Set<Long> findLikedLoungePostIds(List<Long> loungePostIds, UserId userId) {
    return Set.copyOf(jpaRepository.findLikedLoungePostIds(loungePostIds, userId.value()));
  }
}
