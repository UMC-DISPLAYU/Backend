package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkFeelingLikeRepository {

  PersonalArtworkFeelingLike save(PersonalArtworkFeelingLike personalArtworkFeelingLike);

  Optional<PersonalArtworkFeelingLike> findByPersonalFeelingIdAndUserId(
      Long personalFeelingId, Long userId);

  long countByPersonalFeelingIdAndDeletedAtIsNull(Long personalFeelingId);

  Map<Long, Long> countByPersonalFeelingIds(List<Long> personalFeelingIds);

  Set<Long> findLikedPersonalFeelingIds(List<Long> personalFeelingIds, Long userId);
}
