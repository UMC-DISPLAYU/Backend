package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingReplyLikeRepositoryAdapter
    implements PersonalArtworkFeelingReplyLikeRepository {

  private final SpringDataPersonalArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public Optional<PersonalArtworkFeelingReplyLikeSnapshot> toggleAndGetSnapshot(
      Long personalFeelingReplyId, Long userId) {
    repository.toggle(personalFeelingReplyId, userId);

    long likeCount =
        repository.countByPersonalFeelingReplyIdAndDeletedAtIsNull(personalFeelingReplyId);
    return repository
        .findByPersonalFeelingReplyIdAndUserId(personalFeelingReplyId, userId)
        .map(replyLike -> toSnapshot(replyLike, likeCount));
  }

  @Override
  public Map<Long, Long> countByPersonalFeelingReplyIds(List<Long> personalFeelingReplyIds) {
    return repository.countByPersonalFeelingReplyIds(personalFeelingReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }

  @Override
  public Set<Long> findLikedPersonalFeelingReplyIds(
      List<Long> personalFeelingReplyIds, Long userId) {
    return Set.copyOf(repository.findLikedPersonalFeelingReplyIds(personalFeelingReplyIds, userId));
  }

  private PersonalArtworkFeelingReplyLikeSnapshot toSnapshot(
      PersonalArtworkFeelingReplyLike replyLike, long likeCount) {
    return new PersonalArtworkFeelingReplyLikeSnapshot(
        replyLike.getPersonalFeelingReplyId(),
        !replyLike.isDeleted(),
        likeCount,
        replyLike.getCreatedAt(),
        replyLike.getDeletedAt());
  }
}
