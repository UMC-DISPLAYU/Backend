package com.example.demo.domain.personalartworkcommunication.domain.repository;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PersonalArtworkFeelingReplyLikeRepository {

  PersonalArtworkFeelingReplyLike save(
      PersonalArtworkFeelingReplyLike personalArtworkFeelingReplyLike);

  Optional<PersonalArtworkFeelingReplyLike> findByPersonalFeelingReplyIdAndUserId(
      Long personalFeelingReplyId, Long userId);

  long countByPersonalFeelingReplyIdAndDeletedAtIsNull(Long personalFeelingReplyId);

  Map<Long, Long> countByPersonalFeelingReplyIds(List<Long> personalFeelingReplyIds);

  Set<Long> findLikedPersonalFeelingReplyIds(List<Long> personalFeelingReplyIds, Long userId);
}
