package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkQuestionJpaRepository
    extends JpaRepository<PersonalArtworkQuestion, Long> {

  Optional<PersonalArtworkQuestion> findByPersonalQuestionIdAndDeletedAtIsNull(
      Long personalQuestionId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT question
      FROM PersonalArtworkQuestion question
      WHERE question.personalQuestionId = :personalQuestionId
        AND question.deletedAt IS NULL
      """)
  Optional<PersonalArtworkQuestion> findActiveByIdForUpdate(
      @Param("personalQuestionId") Long personalQuestionId);

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
