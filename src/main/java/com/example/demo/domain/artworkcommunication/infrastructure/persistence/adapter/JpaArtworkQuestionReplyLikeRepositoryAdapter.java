package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
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
  public ArtworkQuestionReplyLike save(ArtworkQuestionReplyLike artworkQuestionReplyLike) {
    return artworkQuestionReplyLikeJpaRepository.save(artworkQuestionReplyLike);
  }

  @Override
  public java.util.Optional<ArtworkQuestionReplyLike> findByQuestionReplyIdAndUserId(
      Long questionReplyId, Long userId) {
    return artworkQuestionReplyLikeJpaRepository.findByQuestionReplyIdAndUserId(
        questionReplyId, userId);
  }

  @Override
  public long countByQuestionReplyIdAndDeletedAtIsNull(Long questionReplyId) {
    return artworkQuestionReplyLikeJpaRepository.countByQuestionReplyIdAndDeletedAtIsNull(
        questionReplyId);
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
