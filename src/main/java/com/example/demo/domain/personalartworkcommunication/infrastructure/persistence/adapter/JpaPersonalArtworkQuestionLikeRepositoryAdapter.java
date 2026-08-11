package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository.PersonalArtworkQuestionLikeSnapshot;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkQuestionLikeJpaRepository;
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

  private final SpringDataPersonalArtworkQuestionLikeJpaRepository
      personalArtworkQuestionLikeJpaRepository;

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
