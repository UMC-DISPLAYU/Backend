package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository.PersonalArtworkQuestionLikeSnapshot;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkQuestionLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionLikeRepositoryAdapter
    implements PersonalArtworkQuestionLikeRepository {

  private final PersonalArtworkQuestionLikeJpaRepository personalArtworkQuestionLikeJpaRepository;

  @Override
  public Optional<PersonalArtworkQuestionLikeSnapshot> toggleAndGetSnapshot(
      Long personalQuestionId, Long userId) {
    personalArtworkQuestionLikeJpaRepository.toggle(personalQuestionId, userId);

    long likeCount =
        personalArtworkQuestionLikeJpaRepository.countByPersonalQuestionIdAndDeletedAtIsNull(
            personalQuestionId);
    return personalArtworkQuestionLikeJpaRepository
        .findByPersonalQuestionIdAndUserId(personalQuestionId, userId)
        .map(questionLike -> toSnapshot(questionLike, likeCount));
  }

  private PersonalArtworkQuestionLikeSnapshot toSnapshot(
      PersonalArtworkQuestionLike questionLike, long likeCount) {
    return new PersonalArtworkQuestionLikeSnapshot(
        questionLike.getPersonalQuestionId(),
        !questionLike.isDeleted(),
        likeCount,
        questionLike.getCreatedAt(),
        questionLike.getDeletedAt());
  }
}
