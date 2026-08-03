package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
      SELECT MAX(artwork.workSortOrder)
      FROM DisplayArtwork artwork
      WHERE artwork.display.id = :displayId
      """)
  Optional<Integer> findMaxWorkSortOrderByDisplayId(@Param("displayId") Long displayId);

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
      WHERE artwork.display.id = :displayId
        AND artwork.deletedAt IS NULL
        AND artwork.status = com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.PUBLISHED
        AND artwork.display.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
      """)
  List<DisplayArtwork> findPublishedByDisplayId(@Param("displayId") Long displayId);

  @Query(
      """
      SELECT artwork
      FROM DisplayArtwork artwork
      JOIN FETCH artwork.display display
      WHERE artwork.deletedAt IS NULL
        AND artwork.status = com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.PUBLISHED
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

  @Modifying(flushAutomatically = true)
  @Query(
      """
      UPDATE DisplayArtwork artwork
      SET artwork.status = :publishedStatus
      WHERE artwork.status = :draftStatus
        AND artwork.deletedAt IS NULL
        AND artwork.display.status = :displayStatus
        AND artwork.display.artworkContentOpen = :openPolicy
        AND artwork.display.period.startDate <= :today
      """)
  int publishOnExhibition(
      @Param("today") LocalDate today,
      @Param("displayStatus") DisplayStatus displayStatus,
      @Param("openPolicy") ContentOpenPolicy openPolicy,
      @Param("draftStatus") DisplayArtworkStatus draftStatus,
      @Param("publishedStatus") DisplayArtworkStatus publishedStatus);

  @Modifying(flushAutomatically = true)
  @Query(
      """
      UPDATE DisplayArtwork artwork
      SET artwork.status = :publishedStatus
      WHERE artwork.status = :draftStatus
        AND artwork.deletedAt IS NULL
        AND artwork.display.id = :displayId
        AND artwork.display.artworkContentOpen = :openPolicy
        AND artwork.display.period.startDate <= :today
      """)
  int publishForDisplay(
      @Param("displayId") Long displayId,
      @Param("today") LocalDate today,
      @Param("openPolicy") ContentOpenPolicy openPolicy,
      @Param("draftStatus") DisplayArtworkStatus draftStatus,
      @Param("publishedStatus") DisplayArtworkStatus publishedStatus);
}
