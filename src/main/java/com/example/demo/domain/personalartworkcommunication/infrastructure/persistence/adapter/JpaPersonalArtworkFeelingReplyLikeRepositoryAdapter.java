package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingReplyLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingReplyLikeRepositoryAdapter
    implements PersonalArtworkFeelingReplyLikeRepository {

  private final PersonalArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public Optional<PersonalArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingReplyId, Long userId) {
    repository.lockByPersonalFeelingReplyId(personalFeelingReplyId);
    repository.toggle(personalFeelingReplyId, userId);

    long likeCount =
        repository.countByPersonalFeelingReplyIdAndDeletedAtIsNull(personalFeelingReplyId);
    return repository
        .findByPersonalFeelingReplyIdAndUserId(personalFeelingReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  private PersonalArtworkFeelingReplyLikeSnapshot toSnapshot(
      PersonalArtworkFeelingReplyLike replyLike, long likeCount) {
    return new PersonalArtworkFeelingReplyLikeSnapshot(
        replyLike.getPersonalFeelingReplyId(),
        !replyLike.isDeleted(),
        likeCount,
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
