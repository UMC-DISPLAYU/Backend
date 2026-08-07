package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkFeelingLikeJpaRepository
    extends JpaRepository<PersonalArtworkFeelingLike, Long> {

  Optional<PersonalArtworkFeelingLike> findByPersonalFeelingIdAndUserId(
      Long personalFeelingId, Long userId);

  long countByPersonalFeelingIdAndDeletedAtIsNull(Long personalFeelingId);

  @Query(
      """
      SELECT feelingLike.personalFeelingId, COUNT(feelingLike)
      FROM PersonalArtworkFeelingLike feelingLike
      WHERE feelingLike.personalFeelingId IN :personalFeelingIds
        AND feelingLike.deletedAt IS NULL
      GROUP BY feelingLike.personalFeelingId
      """)
  List<Object[]> countByPersonalFeelingIds(
      @Param("personalFeelingIds") List<Long> personalFeelingIds);

  @Query(
      """
      SELECT feelingLike.personalFeelingId
      FROM PersonalArtworkFeelingLike feelingLike
      WHERE feelingLike.personalFeelingId IN :personalFeelingIds
        AND feelingLike.userId = :userId
        AND feelingLike.deletedAt IS NULL
      """)
  List<Long> findLikedPersonalFeelingIds(
      @Param("personalFeelingIds") List<Long> personalFeelingIds, @Param("userId") Long userId);
}
