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

  /**
   * 해당 유저가 <b>작가로</b> 참여한 출품작을 등록순으로 조회한다.
   *
   * <p>대표 작가({@code LEAD_ARTIST})와 공동 작업자({@code CO_AUTHOR})는 포함하고, QnA 답변만 담당하는 전시 대표자({@code
   * QA_ONLY})는 제외한다. 계정 없는 작가를 대리 등록하면 담당자를 지정하기 위해 대표자에게 {@code QA_ONLY} Creator가 생기는데, 이는 작가로서의
   * 참여가 아니므로 그 사람의 작가 프로필에 남의 작품이 노출되면 안 된다.
   */
  @Query(
      """
      SELECT artwork
      FROM DisplayArtwork artwork
      JOIN FETCH artwork.display display
      WHERE artwork.deletedAt IS NULL
        AND artwork.status = com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.PUBLISHED
        AND display.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
        AND artwork.id IN (
          SELECT creator.displayArtworkId
          FROM Creator creator
          WHERE creator.userId = :userId
            AND creator.role <> com.example.demo.domain.displayartwork.domain.type.CreatorRole.QA_ONLY
        )
      ORDER BY artwork.createdAt ASC, artwork.id ASC
      """)
  List<DisplayArtwork> findAllByParticipantUserId(@Param("userId") Long userId);

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
        AND (:ignoreFields = true OR artwork.type IN :fields)
        AND (:school IS NULL OR display.organization = :school)
      ORDER BY artwork.createdAt DESC
      """)
  List<DisplayArtwork> findPreview(
      @Param("requireGraduation") boolean requireGraduation,
      @Param("ignoreFields") boolean ignoreFields,
      @Param("fields") List<ArtworkType> fields,
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
        AND (artwork.display.artworkContentOpen = :immediatePolicy
             OR artwork.display.period.startDate <= :today)
      """)
  int publishForDisplay(
      @Param("displayId") Long displayId,
      @Param("today") LocalDate today,
      @Param("immediatePolicy") ContentOpenPolicy immediatePolicy,
      @Param("draftStatus") DisplayArtworkStatus draftStatus,
      @Param("publishedStatus") DisplayArtworkStatus publishedStatus);
}
