package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository.ArtworkQuestionReplyLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionReplyLikeRepositoryAdapter
    implements ArtworkQuestionReplyLikeRepository {

  private final ArtworkQuestionReplyLikeJpaRepository artworkQuestionReplyLikeJpaRepository;

  @Override
  public Optional<ArtworkQuestionReplyLikeSnapshot> likeAndGetSnapshot(
      Long questionReplyId, Long userId) {
    artworkQuestionReplyLikeJpaRepository.insertIfAbsent(questionReplyId, userId);

    long likeCount = artworkQuestionReplyLikeJpaRepository.countByQuestionReplyId(questionReplyId);
    return artworkQuestionReplyLikeJpaRepository
        .findByQuestionReplyIdAndUserId(questionReplyId, userId)
        .map(questionReplyLike -> toSnapshot(questionReplyLike, likeCount));
  }

  @Override
  public Optional<ArtworkQuestionReplyLikeSnapshot> deleteAndGetSnapshot(
      Long questionReplyId, Long userId) {
    int deleted =
        artworkQuestionReplyLikeJpaRepository.deleteByQuestionReplyIdAndUserId(
            questionReplyId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = artworkQuestionReplyLikeJpaRepository.countByQuestionReplyId(questionReplyId);
    return Optional.of(
        new ArtworkQuestionReplyLikeSnapshot(questionReplyId, false, likeCount, null, null));
  }

  private ArtworkQuestionReplyLikeSnapshot toSnapshot(
      ArtworkQuestionReplyLike questionReplyLike, long likeCount) {
    return new ArtworkQuestionReplyLikeSnapshot(
        questionReplyLike.getQuestionReplyId(),
        true,
        likeCount,
        questionReplyLike.getCreatedAt(),
        null);
  }

  @Override
  public Map<Long, Long> countByQuestionReplyIds(List<Long> questionReplyIds) {
    if (questionReplyIds.isEmpty()) {
      return Map.of();
    }

    return artworkQuestionReplyLikeJpaRepository.countByQuestionReplyIds(questionReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedQuestionReplyIds(List<Long> questionReplyIds, Long userId) {
    if (questionReplyIds.isEmpty()) {
      return Set.of();
    }

    return Set.copyOf(
        artworkQuestionReplyLikeJpaRepository.findLikedQuestionReplyIds(questionReplyIds, userId));
  }
}
