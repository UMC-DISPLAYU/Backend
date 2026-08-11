package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryRepository;
import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryResult;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 이 어댑터의 결과는 archive 도메인이 "작품이 존재하는가"를 판단하는 근거로 쓰인다. DisplayArtwork는 소프트 삭제라 삭제 후에도 row가 남으므로, 삭제분이
 * 결과에 섞이면 삭제된 작품이 아카이브 목록에 노출되고 저장까지 통과한다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({
  JpaArtworkSummaryQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaArtworkSummaryQueryRepositoryAdapterTest {

  private static final Long OWNER = 1L;
  private static final Long LIVING_ARTWORK_ID = 100L;
  private static final Long DELETED_ARTWORK_ID = 200L;

  @Autowired private ArtworkSummaryQueryRepository queryRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void findByDisplayArtworkIdInExcludesDeletedArtwork() {
    Long displayId = persistedDisplayId();
    insertArtwork(LIVING_ARTWORK_ID, displayId, "살아있는 작품", null);
    insertArtwork(DELETED_ARTWORK_ID, displayId, "삭제된 작품", LocalDateTime.of(2026, 8, 1, 12, 0));

    List<ArtworkSummaryQueryResult> results =
        queryRepository.findByDisplayArtworkIdIn(List.of(LIVING_ARTWORK_ID, DELETED_ARTWORK_ID));

    assertThat(results)
        .extracting(ArtworkSummaryQueryResult::displayArtworkId)
        .containsExactly(LIVING_ARTWORK_ID);
  }

  @Test
  void findByDisplayArtworkIdInReturnsEmptyWhenAllArtworksAreDeleted() {
    Long displayId = persistedDisplayId();
    insertArtwork(DELETED_ARTWORK_ID, displayId, "삭제된 작품", LocalDateTime.of(2026, 8, 1, 12, 0));

    // archive 도메인이 "결과가 비어있으면 존재하지 않는다"로 판단하므로 빈 결과여야 저장이 차단된다.
    assertThat(queryRepository.findByDisplayArtworkIdIn(List.of(DELETED_ARTWORK_ID))).isEmpty();
  }

  private Long persistedDisplayId() {
    Display display =
        Display.create(
            new UserId(OWNER),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    entityManager.persist(display);
    entityManager.flush();
    return display.getId();
  }

  /**
   * artworkcommunication의 DisplayArtworkReferenceJpaEntity가 같은 테이블에 @GeneratedValue 없이 매핑돼 있어, 테스트
   * 스키마에서는 ID가 auto increment로 생성되지 않는다. 그래서 ID를 직접 지정해 넣는다.
   */
  private void insertArtwork(
      Long artworkId, Long displayId, String artworkName, LocalDateTime deletedAt) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO display_artwork
              (display_artwork_id, display_id, artwork_name, content, type, production_year,
               material_media, size, point, work_sort_order, registered_by_user_id, status,
               created_at, updated_at, deleted_at)
            VALUES (?, ?, ?, 'content', 'PAINTING', 2026, 'Oil on canvas', '72.7 x 90.9 cm',
                    'point', 0, ?, 'PUBLISHED', ?, ?, ?)
            """)
        .setParameter(1, artworkId)
        .setParameter(2, displayId)
        .setParameter(3, artworkName)
        .setParameter(4, OWNER)
        .setParameter(5, LocalDateTime.of(2026, 7, 1, 10, 0))
        .setParameter(6, LocalDateTime.of(2026, 7, 1, 10, 0))
        .setParameter(7, deletedAt)
        .executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }
}
