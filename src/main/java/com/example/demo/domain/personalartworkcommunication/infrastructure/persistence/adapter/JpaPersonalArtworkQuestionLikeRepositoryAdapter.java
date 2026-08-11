package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository.PersonalArtworkQuestionLikeSnapshot;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkQuestionLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionLikeRepositoryAdapter
    implements PersonalArtworkQuestionLikeRepository {

  private final PersonalArtworkQuestionLikeJpaRepository personalArtworkQuestionLikeJpaRepository;

  @Override
  public Optional<PersonalArtworkQuestionLikeSnapshot> likeAndGetSnapshot(
      Long personalQuestionId, Long userId) {
    personalArtworkQuestionLikeJpaRepository.insertIfAbsent(personalQuestionId, userId);

    long likeCount =
        personalArtworkQuestionLikeJpaRepository.countByPersonalQuestionId(personalQuestionId);
    return personalArtworkQuestionLikeJpaRepository
        .findByPersonalQuestionIdAndUserId(personalQuestionId, userId)
        .map(questionLike -> toSnapshot(questionLike, likeCount));
  }

  @Override
  public Optional<PersonalArtworkQuestionLikeSnapshot> deleteAndGetSnapshot(
      Long personalQuestionId, Long userId) {
    int deleted =
        personalArtworkQuestionLikeJpaRepository.deleteByPersonalQuestionIdAndUserId(
            personalQuestionId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount =
        personalArtworkQuestionLikeJpaRepository.countByPersonalQuestionId(personalQuestionId);
    return Optional.of(
        new PersonalArtworkQuestionLikeSnapshot(personalQuestionId, false, likeCount, null, null));
  }

  private PersonalArtworkQuestionLikeSnapshot toSnapshot(
      PersonalArtworkQuestionLike questionLike, long likeCount) {
    return new PersonalArtworkQuestionLikeSnapshot(
        questionLike.getPersonalQuestionId(), true, likeCount, questionLike.getCreatedAt(), null);
  }

  @Override
  public Map<Long, Long> countByPersonalQuestionIds(List<Long> personalQuestionIds) {
    if (personalQuestionIds.isEmpty()) {
      return Map.of();
    }

    return personalArtworkQuestionLikeJpaRepository
        .countByPersonalQuestionIds(personalQuestionIds)
        .stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedPersonalQuestionIds(List<Long> personalQuestionIds, Long userId) {
    if (personalQuestionIds.isEmpty()) {
      return Set.of();
    }

    return Set.copyOf(
        personalArtworkQuestionLikeJpaRepository.findLikedPersonalQuestionIds(
            personalQuestionIds, userId));
  }
}
