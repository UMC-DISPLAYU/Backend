package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReplyLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionReplyLikeJpaRepository
    extends JpaRepository<PersonalArtworkQuestionReplyLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkQuestionReplyLike
            (createdAt, updatedAt, deletedAt, personalQuestionReplyId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalQuestionReplyId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(
      @Param("personalQuestionReplyId") Long personalQuestionReplyId, @Param("userId") Long userId);

  Optional<PersonalArtworkQuestionReplyLike> findByPersonalQuestionReplyIdAndUserId(
      Long personalQuestionReplyId, Long userId);

  long countByPersonalQuestionReplyIdAndDeletedAtIsNull(Long personalQuestionReplyId);
}
