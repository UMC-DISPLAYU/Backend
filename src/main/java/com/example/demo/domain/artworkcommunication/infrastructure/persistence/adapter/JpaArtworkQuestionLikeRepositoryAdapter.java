package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionLikeRepositoryAdapter implements ArtworkQuestionLikeRepository {

  private final ArtworkQuestionLikeJpaRepository artworkQuestionLikeJpaRepository;

  @Override
  public ArtworkQuestionLike save(ArtworkQuestionLike artworkQuestionLike) {
    return artworkQuestionLikeJpaRepository.save(artworkQuestionLike);
  }

  @Override
  public java.util.Optional<ArtworkQuestionLike> findByQuestionIdAndUserId(
      Long questionId, Long userId) {
    return artworkQuestionLikeJpaRepository.findByQuestionIdAndUserId(questionId, userId);
  }

  @Override
  public long countByQuestionIdAndDeletedAtIsNull(Long questionId) {
    return artworkQuestionLikeJpaRepository.countByQuestionIdAndDeletedAtIsNull(questionId);
  }

  @Override
  public Map<Long, Long> countByQuestionIds(List<Long> questionIds) {
    if (questionIds.isEmpty()) {
      return Map.of();
    }

    return artworkQuestionLikeJpaRepository.countByQuestionIds(questionIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }
}
