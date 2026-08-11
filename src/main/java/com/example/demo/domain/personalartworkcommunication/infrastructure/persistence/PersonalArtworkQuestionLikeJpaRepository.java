package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionLike;
import java.util.List;
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
            personalQuestionLikeId = personalQuestionLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(
      @Param("personalQuestionId") Long personalQuestionId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM PersonalArtworkQuestionLike questionLike
      WHERE questionLike.personalQuestionId = :personalQuestionId
        AND questionLike.userId = :userId
      """)
  int deleteByPersonalQuestionIdAndUserId(
      @Param("personalQuestionId") Long personalQuestionId, @Param("userId") Long userId);

  Optional<PersonalArtworkQuestionLike> findByPersonalQuestionIdAndUserId(
      Long personalQuestionId, Long userId);

  long countByPersonalQuestionId(Long personalQuestionId);

  @Query(
      """
      SELECT questionLike.personalQuestionId, COUNT(questionLike)
      FROM PersonalArtworkQuestionLike questionLike
      WHERE questionLike.personalQuestionId IN :personalQuestionIds
      GROUP BY questionLike.personalQuestionId
      """)
  List<Object[]> countByPersonalQuestionIds(
      @Param("personalQuestionIds") List<Long> personalQuestionIds);

  @Query(
      """
      SELECT questionLike.personalQuestionId
      FROM PersonalArtworkQuestionLike questionLike
      WHERE questionLike.personalQuestionId IN :personalQuestionIds
        AND questionLike.userId = :userId
      """)
  List<Long> findLikedPersonalQuestionIds(
      @Param("personalQuestionIds") List<Long> personalQuestionIds, @Param("userId") Long userId);
}
