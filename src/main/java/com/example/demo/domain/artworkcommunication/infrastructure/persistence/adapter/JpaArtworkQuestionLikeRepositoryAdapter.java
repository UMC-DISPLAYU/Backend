package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionLikeRepository.ArtworkQuestionLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionLikeRepositoryAdapter implements ArtworkQuestionLikeRepository {

  private final ArtworkQuestionLikeJpaRepository artworkQuestionLikeJpaRepository;

  @Override
  public Optional<ArtworkQuestionLikeSnapshot> toggleAndGetSnapshot(Long questionId, Long userId) {
    artworkQuestionLikeJpaRepository.toggle(questionId, userId);

    long likeCount =
        artworkQuestionLikeJpaRepository.countByQuestionIdAndDeletedAtIsNull(questionId);
    return artworkQuestionLikeJpaRepository
        .findByQuestionIdAndUserId(questionId, userId)
        .map(questionLike -> toSnapshot(questionLike, likeCount));
  }

  private ArtworkQuestionLikeSnapshot toSnapshot(ArtworkQuestionLike questionLike, long likeCount) {
    return new ArtworkQuestionLikeSnapshot(
        questionLike.getQuestionId(),
        !questionLike.isDeleted(),
        likeCount,
        questionLike.getCreatedAt(),
        questionLike.getDeletedAt());
  }
}
