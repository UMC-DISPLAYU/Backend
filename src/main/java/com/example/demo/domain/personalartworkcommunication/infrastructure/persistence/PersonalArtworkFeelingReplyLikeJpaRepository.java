package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReplyLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkFeelingReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkFeelingReplyLike, Long> {

  Optional<PersonalArtworkFeelingReplyLike> findByPersonalFeelingReplyIdAndUserId(
      Long personalFeelingReplyId, Long userId);

  long countByPersonalFeelingReplyIdAndDeletedAtIsNull(Long personalFeelingReplyId);

  @Query(
      """
      SELECT replyLike.personalFeelingReplyId, COUNT(replyLike)
      FROM PersonalArtworkFeelingReplyLike replyLike
      WHERE replyLike.personalFeelingReplyId IN :personalFeelingReplyIds
        AND replyLike.deletedAt IS NULL
      GROUP BY replyLike.personalFeelingReplyId
      """)
  List<Object[]> countByPersonalFeelingReplyIds(
      @Param("personalFeelingReplyIds") List<Long> personalFeelingReplyIds);
}
