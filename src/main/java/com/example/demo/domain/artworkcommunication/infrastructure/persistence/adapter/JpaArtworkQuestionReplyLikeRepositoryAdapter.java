package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReplyLike;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyLikeRepository.ArtworkQuestionReplyLikeSnapshot;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.ArtworkQuestionReplyLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaArtworkQuestionReplyLikeRepositoryAdapter
    implements ArtworkQuestionReplyLikeRepository {

  private final ArtworkQuestionReplyLikeJpaRepository artworkQuestionReplyLikeJpaRepository;

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
}
