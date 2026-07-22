package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionJpaRepository
    extends JpaRepository<PersonalArtworkQuestion, Long> {

  @Query(
      """
      SELECT question
      FROM PersonalArtworkQuestion question
      WHERE question.personalArtworkId = :personalArtworkId
        AND question.deletedAt IS NULL
        AND (:cursorId IS NULL OR question.personalQuestionId > :cursorId)
      ORDER BY question.personalQuestionId ASC
      """)
  List<PersonalArtworkQuestion> findActiveByPersonalArtworkIdWithCursor(
      @Param("personalArtworkId") Long personalArtworkId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
