package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.PersonalArtworkFeelingReplyLikeJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaPersonalArtworkFeelingReplyLikeRepositoryAdapter
    implements PersonalArtworkFeelingReplyLikeRepository {

  private final PersonalArtworkFeelingReplyLikeJpaRepository repository;

  @Override
  public PersonalArtworkFeelingReplyLike save(
      PersonalArtworkFeelingReplyLike personalArtworkFeelingReplyLike) {
    return repository.save(personalArtworkFeelingReplyLike);
  }

  @Override
  public java.util.Optional<PersonalArtworkFeelingReplyLike> findByPersonalFeelingReplyIdAndUserId(
      Long personalFeelingReplyId, Long userId) {
    return repository.findByPersonalFeelingReplyIdAndUserId(personalFeelingReplyId, userId);
  }

  @Override
  public long countByPersonalFeelingReplyIdAndDeletedAtIsNull(Long personalFeelingReplyId) {
    return repository.countByPersonalFeelingReplyIdAndDeletedAtIsNull(personalFeelingReplyId);
  }

  @Override
  public Map<Long, Long> countByPersonalFeelingReplyIds(List<Long> personalFeelingReplyIds) {
    return repository.countByPersonalFeelingReplyIds(personalFeelingReplyIds).stream()
        .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
  }
}
