package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkQuestionReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkQuestionReplyLikeRepositoryAdapter
    implements PersonalArtworkQuestionReplyLikeRepository {

  private final PersonalArtworkQuestionReplyLikeJpaRepository repository;

  @Override
  public PersonalArtworkQuestionReplyLike save(
      PersonalArtworkQuestionReplyLike personalArtworkQuestionReplyLike) {
    return repository.save(personalArtworkQuestionReplyLike);
  }

  @Override
  public java.util.Optional<PersonalArtworkQuestionReplyLike>
      findByPersonalQuestionReplyIdAndUserId(Long personalQuestionReplyId, Long userId) {
    return repository.findByPersonalQuestionReplyIdAndUserId(personalQuestionReplyId, userId);
  }

  @Override
  public long countByPersonalQuestionReplyIdAndDeletedAtIsNull(Long personalQuestionReplyId) {
    return repository.countByPersonalQuestionReplyIdAndDeletedAtIsNull(personalQuestionReplyId);
  }

  @Override
  public Map<Long, Long> countByPersonalQuestionReplyIds(List<Long> personalQuestionReplyIds) {
    if (personalQuestionReplyIds.isEmpty()) {
      return Map.of();
    }

    return repository.countByPersonalQuestionReplyIds(personalQuestionReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedPersonalQuestionReplyIds(
      List<Long> personalQuestionReplyIds, Long userId) {
    if (personalQuestionReplyIds.isEmpty()) {
      return Set.of();
    }

    return Set.copyOf(
        repository.findLikedPersonalQuestionReplyIds(personalQuestionReplyIds, userId));
  }
}
