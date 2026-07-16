package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkFeelingJpaRepository extends JpaRepository<ArtworkFeeling, Long> {

  List<ArtworkFeeling> findByDisplayArtworkIdAndDeletedAtIsNullOrderByCreatedAtAsc(
      Long displayArtworkId);

  @Query(
      """
      SELECT feeling
      FROM ArtworkFeeling feeling
      WHERE feeling.displayArtworkId = :displayArtworkId
        AND feeling.deletedAt IS NULL
        AND (:cursorId IS NULL OR feeling.feelingId > :cursorId)
      ORDER BY feeling.feelingId ASC
      """)
  List<ArtworkFeeling> findActiveByDisplayArtworkIdWithCursor(
      @Param("displayArtworkId") Long displayArtworkId,
      @Param("cursorId") Long cursorId,
      Pageable pageable);
}
