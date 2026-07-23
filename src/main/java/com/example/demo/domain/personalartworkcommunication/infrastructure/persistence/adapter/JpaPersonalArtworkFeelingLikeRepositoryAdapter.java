package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingLikeRepositoryAdapter
    implements PersonalArtworkFeelingLikeRepository {

  private final PersonalArtworkFeelingLikeJpaRepository repository;

  @Override
  public Optional<PersonalArtworkFeelingLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingId, Long userId) {
    repository.lockByPersonalFeelingId(personalFeelingId);
    repository.toggle(personalFeelingId, userId);

    long likeCount = repository.countByPersonalFeelingIdAndDeletedAtIsNull(personalFeelingId);
    return repository
        .findByPersonalFeelingIdAndUserId(personalFeelingId, userId)
        .map(feelingLike -> toSnapshot(feelingLike, likeCount));
  }

  private PersonalArtworkFeelingLikeSnapshot toSnapshot(
      PersonalArtworkFeelingLike feelingLike, long likeCount) {
    return new PersonalArtworkFeelingLikeSnapshot(
        feelingLike.getPersonalFeelingId(),
        !feelingLike.isDeleted(),
        likeCount,
        feelingLike.getCreatedAt(),
        feelingLike.getDeletedAt());
  }
}
