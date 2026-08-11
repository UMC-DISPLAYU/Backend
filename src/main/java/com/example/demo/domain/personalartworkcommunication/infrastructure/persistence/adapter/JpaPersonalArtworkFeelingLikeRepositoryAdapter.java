package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingLikeJpaRepository;
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

  private final SpringDataPersonalArtworkFeelingLikeJpaRepository repository;

  @Override
  public Optional<PersonalArtworkFeelingLikeSnapshot> likeAndGetSnapshot(
      Long personalFeelingId, Long userId) {
    repository.lockByPersonalFeelingId(personalFeelingId);
    repository.insertIfAbsent(personalFeelingId, userId);

    long likeCount = repository.countByPersonalFeelingId(personalFeelingId);
    return repository
        .findByPersonalFeelingIdAndUserId(personalFeelingId, userId)
        .map(feelingLike -> toSnapshot(feelingLike, likeCount));
  }

  @Override
  public Optional<PersonalArtworkFeelingLikeSnapshot> deleteAndGetSnapshot(
      Long personalFeelingId, Long userId) {
    repository.lockByPersonalFeelingId(personalFeelingId);
    int deleted = repository.deleteByPersonalFeelingIdAndUserId(personalFeelingId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = repository.countByPersonalFeelingId(personalFeelingId);
    return Optional.of(
        new PersonalArtworkFeelingLikeSnapshot(personalFeelingId, false, likeCount, null, null));
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
        feelingLike.getPersonalFeelingId(), true, likeCount, feelingLike.getCreatedAt(), null);
  }
}
