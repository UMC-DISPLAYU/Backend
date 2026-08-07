package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewLikeJpaRepository extends JpaRepository<DisplayReviewLike, Long> {

  Optional<DisplayReviewLike> findByDisplayReviewIdAndUserId(Long displayReviewId, Long userId);

  long countByDisplayReviewIdAndDeletedAtIsNull(Long displayReviewId);

  @Query(
      """
      SELECT reviewLike.displayReviewId, COUNT(reviewLike)
      FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId IN :displayReviewIds
        AND reviewLike.deletedAt IS NULL
      GROUP BY reviewLike.displayReviewId
      """)
  List<Object[]> countByDisplayReviewIds(@Param("displayReviewIds") List<Long> displayReviewIds);

  @Query(
      """
      SELECT reviewLike.displayReviewId
      FROM DisplayReviewLike reviewLike
      WHERE reviewLike.displayReviewId IN :displayReviewIds
        AND reviewLike.userId = :userId
        AND reviewLike.deletedAt IS NULL
      """)
  List<Long> findLikedDisplayReviewIds(
      @Param("displayReviewIds") List<Long> displayReviewIds, @Param("userId") Long userId);
}
