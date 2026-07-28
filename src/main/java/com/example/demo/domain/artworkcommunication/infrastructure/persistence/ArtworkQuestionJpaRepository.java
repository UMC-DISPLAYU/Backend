package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionJpaRepository extends JpaRepository<ArtworkQuestion, Long> {

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
