package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkFeelingLikeJpaRepository extends JpaRepository<ArtworkFeelingLike, Long> {

  Optional<ArtworkFeelingLike> findByFeelingIdAndUserId(Long feelingId, Long userId);

  long countByFeelingIdAndDeletedAtIsNull(Long feelingId);

  @Query(
      """
      SELECT feelingLike.feelingId, COUNT(feelingLike)
      FROM ArtworkFeelingLike feelingLike
      WHERE feelingLike.feelingId IN :feelingIds
        AND feelingLike.deletedAt IS NULL
      GROUP BY feelingLike.feelingId
      """)
  List<Object[]> countByFeelingIds(@Param("feelingIds") List<Long> feelingIds);

  @Query(
      """
      SELECT feelingLike.feelingId
      FROM ArtworkFeelingLike feelingLike
      WHERE feelingLike.feelingId IN :feelingIds
        AND feelingLike.userId = :userId
        AND feelingLike.deletedAt IS NULL
      """)
  List<Long> findLikedFeelingIds(
      @Param("feelingIds") List<Long> feelingIds, @Param("userId") Long userId);
}
