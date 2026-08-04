package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionLikeJpaRepository
    extends JpaRepository<PersonalArtworkQuestionLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO PersonalArtworkQuestionLike
            (createdAt, updatedAt, deletedAt, personalQuestionId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :personalQuestionId, :userId)
          ON DUPLICATE KEY UPDATE
            updatedAt = CURRENT_TIMESTAMP,
            deletedAt = IF(deletedAt IS NULL, CURRENT_TIMESTAMP, NULL)
          """,
      nativeQuery = true)
  void toggle(@Param("personalQuestionId") Long personalQuestionId, @Param("userId") Long userId);

  Optional<PersonalArtworkQuestionLike> findByPersonalQuestionIdAndUserId(
      Long personalQuestionId, Long userId);

  long countByPersonalQuestionIdAndDeletedAtIsNull(Long personalQuestionId);
}
