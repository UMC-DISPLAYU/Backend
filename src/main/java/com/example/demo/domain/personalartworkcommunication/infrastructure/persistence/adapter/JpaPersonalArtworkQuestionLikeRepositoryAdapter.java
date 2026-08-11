package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
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
  public PersonalArtworkQuestionLike save(PersonalArtworkQuestionLike personalArtworkQuestionLike) {
    return personalArtworkQuestionLikeJpaRepository.save(personalArtworkQuestionLike);
  }

  @Override
  public java.util.Optional<PersonalArtworkQuestionLike> findByPersonalQuestionIdAndUserId(
      Long personalQuestionId, Long userId) {
    return personalArtworkQuestionLikeJpaRepository.findByPersonalQuestionIdAndUserId(
        personalQuestionId, userId);
  }

  @Override
  public long countByPersonalQuestionIdAndDeletedAtIsNull(Long personalQuestionId) {
    return personalArtworkQuestionLikeJpaRepository.countByPersonalQuestionIdAndDeletedAtIsNull(
        personalQuestionId);
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
