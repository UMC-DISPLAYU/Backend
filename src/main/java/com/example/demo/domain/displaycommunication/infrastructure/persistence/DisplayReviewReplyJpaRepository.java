package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayReviewReplyJpaRepository extends JpaRepository<DisplayReviewReply, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          UPDATE DisplayReviewReply
          SET updatedAt = CURRENT_TIMESTAMP,
              deletedAt = CURRENT_TIMESTAMP
          WHERE displayReviewId = :displayReviewId
            AND deletedAt IS NULL
          """,
      nativeQuery = true)
  void softDeleteAllByDisplayReviewId(@Param("displayReviewId") Long displayReviewId);

  List<DisplayReviewReply> findByDisplayReviewIdInAndDeletedAtIsNullOrderByDisplayReviewReplyIdAsc(
      List<Long> displayReviewIds);
}
