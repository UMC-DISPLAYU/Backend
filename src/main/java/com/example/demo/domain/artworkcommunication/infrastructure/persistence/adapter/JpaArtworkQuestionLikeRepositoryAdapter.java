package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository.ArtworkQuestionLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionLikeRepositoryAdapter implements ArtworkQuestionLikeRepository {

  private final ArtworkQuestionLikeJpaRepository artworkQuestionLikeJpaRepository;

  @Override
  public Optional<ArtworkQuestionLikeSnapshot> likeAndGetSnapshot(Long questionId, Long userId) {
    artworkQuestionLikeJpaRepository.insertIfAbsent(questionId, userId);

    long likeCount = artworkQuestionLikeJpaRepository.countByQuestionId(questionId);
    return artworkQuestionLikeJpaRepository
        .findByQuestionIdAndUserId(questionId, userId)
        .map(questionLike -> toSnapshot(questionLike, likeCount));
  }

  @Override
  public Optional<ArtworkQuestionLikeSnapshot> deleteAndGetSnapshot(Long questionId, Long userId) {
    int deleted = artworkQuestionLikeJpaRepository.deleteByQuestionIdAndUserId(questionId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = artworkQuestionLikeJpaRepository.countByQuestionId(questionId);
    return Optional.of(new ArtworkQuestionLikeSnapshot(questionId, false, likeCount, null, null));
  }

  private ArtworkQuestionLikeSnapshot toSnapshot(ArtworkQuestionLike questionLike, long likeCount) {
    return new ArtworkQuestionLikeSnapshot(
        questionLike.getQuestionId(), true, likeCount, questionLike.getCreatedAt(), null);
  }

  @Override
  public Map<Long, Long> countByQuestionIds(List<Long> questionIds) {
    if (questionIds.isEmpty()) {
      return Map.of();
    }

    return artworkQuestionLikeJpaRepository.countByQuestionIds(questionIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedQuestionIds(List<Long> questionIds, Long userId) {
    if (questionIds.isEmpty()) {
      return Set.of();
    }

    return Set.copyOf(artworkQuestionLikeJpaRepository.findLikedQuestionIds(questionIds, userId));
  }
}
