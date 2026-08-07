package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingLikeRepositoryAdapter
    implements PersonalArtworkFeelingLikeRepository {

  private final PersonalArtworkFeelingLikeJpaRepository repository;

  @Override
  public PersonalArtworkFeelingLike save(PersonalArtworkFeelingLike personalArtworkFeelingLike) {
    return repository.save(personalArtworkFeelingLike);
  }

  @Override
  public java.util.Optional<PersonalArtworkFeelingLike> findByPersonalFeelingIdAndUserId(
      Long personalFeelingId, Long userId) {
    return repository.findByPersonalFeelingIdAndUserId(personalFeelingId, userId);
  }

  @Override
  public long countByPersonalFeelingIdAndDeletedAtIsNull(Long personalFeelingId) {
    return repository.countByPersonalFeelingIdAndDeletedAtIsNull(personalFeelingId);
  }

  @Override
  public Map<Long, Long> countByPersonalFeelingIds(List<Long> personalFeelingIds) {
    return repository.countByPersonalFeelingIds(personalFeelingIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedPersonalFeelingIds(List<Long> personalFeelingIds, Long userId) {
    return Set.copyOf(repository.findLikedPersonalFeelingIds(personalFeelingIds, userId));
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
