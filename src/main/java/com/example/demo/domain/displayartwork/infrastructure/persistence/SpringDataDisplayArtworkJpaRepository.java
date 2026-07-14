package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.util.List;
import org.springframework.data.domain.Pageable;
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

  @Query(
      """
      SELECT artwork
      FROM DisplayArtwork artwork
      WHERE artwork.display.id = :displayId
        AND artwork.deletedAt IS NULL
      """)
  List<DisplayArtwork> findAllByDisplayId(@Param("displayId") Long displayId);

  @Query(
      """
      SELECT artwork
      FROM DisplayArtwork artwork
      JOIN FETCH artwork.display display
      WHERE artwork.deletedAt IS NULL
        AND display.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
        AND (:requireGraduation = false OR display.displayType
             = com.example.demo.domain.display.domain.type.DisplayType.GRADUATION)
        AND (:field IS NULL OR artwork.type = :field)
        AND (:school IS NULL OR display.organization = :school)
      ORDER BY artwork.createdAt DESC
      """)
  List<DisplayArtwork> findPreview(
      @Param("requireGraduation") boolean requireGraduation,
      @Param("field") ArtworkType field,
      @Param("school") String school,
      Pageable pageable);
}
