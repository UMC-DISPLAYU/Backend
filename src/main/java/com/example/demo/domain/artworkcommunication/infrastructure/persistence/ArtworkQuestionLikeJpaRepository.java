package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionLikeJpaRepository extends JpaRepository<ArtworkQuestionLike, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      value =
          """
          INSERT INTO ArtworkQuestionLike
            (createdAt, updatedAt, deletedAt, questionId, userId)
          VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, :questionId, :userId)
          ON DUPLICATE KEY UPDATE
            questionLikeId = questionLikeId
          """,
      nativeQuery = true)
  void insertIfAbsent(@Param("questionId") Long questionId, @Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      DELETE FROM ArtworkQuestionLike questionLike
      WHERE questionLike.questionId = :questionId
        AND questionLike.userId = :userId
      """)
  int deleteByQuestionIdAndUserId(
      @Param("questionId") Long questionId, @Param("userId") Long userId);

  Optional<ArtworkQuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId);

  long countByQuestionId(Long questionId);

  @Query(
      """
      SELECT questionLike.questionId, COUNT(questionLike)
      FROM ArtworkQuestionLike questionLike
      WHERE questionLike.questionId IN :questionIds
      GROUP BY questionLike.questionId
      """)
  List<Object[]> countByQuestionIds(@Param("questionIds") List<Long> questionIds);

  @Query(
      """
      SELECT questionLike.questionId
      FROM ArtworkQuestionLike questionLike
      WHERE questionLike.questionId IN :questionIds
        AND questionLike.userId = :userId
      """)
  List<Long> findLikedQuestionIds(
      @Param("questionIds") List<Long> questionIds, @Param("userId") Long userId);
}
