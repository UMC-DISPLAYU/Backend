package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataDisplayArtworkJpaRepository extends JpaRepository<DisplayArtwork, Long> {

  @Query(
      """
      SELECT COUNT(artwork)
      FROM DisplayArtwork artwork
      WHERE artwork.display.id = :displayId
        AND artwork.deletedAt IS NULL
      """)
  int countByDisplayId(@Param("displayId") Long displayId);
}
