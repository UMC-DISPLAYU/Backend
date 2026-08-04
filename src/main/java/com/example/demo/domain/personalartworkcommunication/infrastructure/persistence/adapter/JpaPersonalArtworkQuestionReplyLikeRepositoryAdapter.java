package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository.PersonalArtworkQuestionReplyLikeSnapshot;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkQuestionReplyLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionReplyLikeRepositoryAdapter
    implements PersonalArtworkQuestionReplyLikeRepository {

  private final PersonalArtworkQuestionReplyLikeJpaRepository repository;

  @Override
  public Optional<PersonalArtworkQuestionReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalQuestionReplyId, Long userId) {
    repository.toggle(personalQuestionReplyId, userId);

    long likeCount =
        repository.countByPersonalQuestionReplyIdAndDeletedAtIsNull(personalQuestionReplyId);
    return repository
        .findByPersonalQuestionReplyIdAndUserId(personalQuestionReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  private PersonalArtworkQuestionReplyLikeSnapshot toSnapshot(
      PersonalArtworkQuestionReplyLike replyLike, long likeCount) {
    return new PersonalArtworkQuestionReplyLikeSnapshot(
        replyLike.getPersonalQuestionReplyId(),
        !replyLike.isDeleted(),
        likeCount,
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
