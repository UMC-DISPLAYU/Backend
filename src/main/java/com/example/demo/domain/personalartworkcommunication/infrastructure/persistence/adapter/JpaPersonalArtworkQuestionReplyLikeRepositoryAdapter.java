package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository.PersonalArtworkQuestionReplyLikeSnapshot;
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
  public Optional<PersonalArtworkQuestionReplyLikeSnapshot> likeAndGetSnapshot(
      Long personalQuestionReplyId, Long userId) {
    repository.insertIfAbsent(personalQuestionReplyId, userId);

    long likeCount = repository.countByPersonalQuestionReplyId(personalQuestionReplyId);
    return repository
        .findByPersonalQuestionReplyIdAndUserId(personalQuestionReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  @Override
  public Optional<PersonalArtworkQuestionReplyLikeSnapshot> deleteAndGetSnapshot(
      Long personalQuestionReplyId, Long userId) {
    int deleted =
        repository.deleteByPersonalQuestionReplyIdAndUserId(personalQuestionReplyId, userId);
    if (deleted == 0) {
      return Optional.empty();
    }
    long likeCount = repository.countByPersonalQuestionReplyId(personalQuestionReplyId);
    return Optional.of(
        new PersonalArtworkQuestionReplyLikeSnapshot(
            personalQuestionReplyId, false, likeCount, null, null));
  }

  private PersonalArtworkQuestionReplyLikeSnapshot toSnapshot(
      PersonalArtworkQuestionReplyLike replyLike, long likeCount) {
    return new PersonalArtworkQuestionReplyLikeSnapshot(
        replyLike.getPersonalQuestionReplyId(), true, likeCount, replyLike.getCreatedAt(), null);
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
