package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository.ArtworkQuestionReplyLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkQuestionReplyLikeJpaRepository;
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

  private final SpringDataArtworkQuestionReplyLikeJpaRepository
      artworkQuestionReplyLikeJpaRepository;

  @Override
  public Optional<ArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long questionReplyId, Long userId) {
    artworkQuestionReplyLikeJpaRepository.toggle(questionReplyId, userId);

    long likeCount =
        artworkQuestionReplyLikeJpaRepository.countByQuestionReplyIdAndDeletedAtIsNull(
            questionReplyId);
    return artworkQuestionReplyLikeJpaRepository
        .findByQuestionReplyIdAndUserId(questionReplyId, userId)
        .map(questionReplyLike -> toSnapshot(questionReplyLike, likeCount));
  }

  private ArtworkQuestionReplyLikeSnapshot toSnapshot(
      ArtworkQuestionReplyLike questionReplyLike, long likeCount) {
    return new ArtworkQuestionReplyLikeSnapshot(
        questionReplyLike.getQuestionReplyId(),
        !questionReplyLike.isDeleted(),
        likeCount,
        questionReplyLike.getCreatedAt(),
        questionReplyLike.getDeletedAt());
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
