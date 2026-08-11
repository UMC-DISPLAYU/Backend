package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataArtworkQuestionJpaRepository
    extends JpaRepository<ArtworkQuestion, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT question
      FROM ArtworkQuestion question
      WHERE question.questionId = :questionId
        AND question.deletedAt IS NULL
      """)
  Optional<ArtworkQuestion> findActiveByIdForUpdate(@Param("questionId") Long questionId);

  @Query(
      """
      SELECT question
      FROM ArtworkQuestion question
      WHERE question.displayArtworkId = :displayArtworkId
        AND question.deletedAt IS NULL
        AND (:cursorId IS NULL OR question.questionId > :cursorId)
      ORDER BY question.questionId ASC
      """)
  List<ArtworkQuestion> findActiveByDisplayArtworkIdWithCursor(
      @Param("displayArtworkId") Long displayArtworkId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
