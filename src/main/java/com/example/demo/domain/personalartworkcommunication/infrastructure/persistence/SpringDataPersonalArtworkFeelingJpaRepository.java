package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonalArtworkFeelingJpaRepository
    extends JpaRepository<PersonalArtworkFeeling, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      SELECT feeling
      FROM PersonalArtworkFeeling feeling
      WHERE feeling.personalFeelingId = :personalFeelingId
        AND feeling.deletedAt IS NULL
      """)
  Optional<PersonalArtworkFeeling> findActiveByIdForUpdate(
      @Param("personalFeelingId") Long personalFeelingId);

  @Query(
      """
      SELECT feeling
      FROM PersonalArtworkFeeling feeling
      WHERE feeling.personalArtworkId = :personalArtworkId
        AND (
          feeling.deletedAt IS NULL
          OR EXISTS (
            SELECT reply.personalFeelingReplyId
            FROM PersonalArtworkFeelingReply reply
            WHERE reply.personalFeelingId = feeling.personalFeelingId
              AND reply.deletedAt IS NULL
          )
        )
        AND (:cursorId IS NULL OR feeling.personalFeelingId > :cursorId)
      ORDER BY feeling.personalFeelingId ASC
      """)
  List<PersonalArtworkFeeling> findByPersonalArtworkIdWithCursorIncludingDeleted(
      @Param("personalArtworkId") Long personalArtworkId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
