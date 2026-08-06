package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalArtworkFeelingJpaRepository
    extends JpaRepository<PersonalArtworkFeeling, Long> {

  @Query(
      """
      SELECT feeling
      FROM PersonalArtworkFeeling feeling
      WHERE feeling.personalArtworkId = :personalArtworkId
        AND (:cursorId IS NULL OR feeling.personalFeelingId > :cursorId)
      ORDER BY feeling.personalFeelingId ASC
      """)
  List<PersonalArtworkFeeling> findByPersonalArtworkIdWithCursorIncludingDeleted(
      @Param("personalArtworkId") Long personalArtworkId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
