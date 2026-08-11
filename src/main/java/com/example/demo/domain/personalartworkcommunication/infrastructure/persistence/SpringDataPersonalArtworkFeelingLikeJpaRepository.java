package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkFeelingLikeJpaRepository
    extends JpaRepository<PersonalArtworkFeelingLike, Long> {

  @Query(
      value =
          """
          SELECT *
          FROM PersonalArtworkFeelingLike
          WHERE personalFeelingId = :personalFeelingId
          FOR UPDATE
          """,
      nativeQuery = true)
  List<PersonalArtworkFeelingLike> lockByPersonalFeelingId(
      @Param("personalFeelingId") Long personalFeelingId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkFeelingLike
            (createdAt, updatedAt, deletedAt, personalFeelingId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalFeelingId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(@Param("personalFeelingId") Long personalFeelingId, @Param("userId") Long userId);

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
