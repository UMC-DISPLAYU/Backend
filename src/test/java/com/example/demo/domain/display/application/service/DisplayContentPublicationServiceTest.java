package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.display.application.query.GetDisplayDetailService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.displayartwork.application.query.DisplayArtworkQueryService;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DisplayContentPublicationServiceTest {

  @Autowired private DisplayContentPublicationService publicationService;

  @Autowired private GetDisplayDetailService getDisplayDetailService;

  @Autowired private DisplayArtworkQueryService displayArtworkQueryService;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void publishOnExhibitionContentsPublishesDraftChildrenWhenDisplayStarted() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    0,
                    DisplayContentStatus.DRAFT))));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_001L;
    insertArtwork(artworkId, savedDisplay.getId(), "공개 대기 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result = publicationService.publishOnExhibitionContents();

    assertThat(result.displayContentCount()).isEqualTo(1);
    assertThat(result.displayArtworkCount()).isEqualTo(1);
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.PUBLISHED);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.PUBLISHED);
  }

  @Test
  void publishOnExhibitionContentsDoesNotPublishWhenDisplayHasNotStarted() {
    Display display =
        display(
            LocalDate.of(2026, 8, 3),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    0,
                    DisplayContentStatus.DRAFT))));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_002L;
    insertArtwork(artworkId, savedDisplay.getId(), "공개 대기 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result = publicationService.publishOnExhibitionContents();

    assertThat(result.displayContentCount()).isZero();
    assertThat(result.displayArtworkCount()).isZero();
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.DRAFT);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.DRAFT);
  }

  @Test
  void publishOnExhibitionContentsDoesNotPublishWhenDisplayIsDraft() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    0,
                    DisplayContentStatus.DRAFT))));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_006L;
    insertArtwork(artworkId, savedDisplay.getId(), "초안 전시 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result = publicationService.publishOnExhibitionContents();

    assertThat(result.displayContentCount()).isZero();
    assertThat(result.displayArtworkCount()).isZero();
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.DRAFT);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.DRAFT);
  }

  @Test
  void publishForDisplayPublishesImmediateChildrenEvenWhenDisplayHasNotStarted() {
    Display display =
        display(
            LocalDate.of(2026, 8, 3), ContentOpenPolicy.IMMEDIATELY, ContentOpenPolicy.IMMEDIATELY);
    display.publish();
    addDraftContent(display);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_007L;
    insertArtwork(artworkId, savedDisplay.getId(), "즉시 공개 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result =
        publicationService.publishForDisplay(savedDisplay.getId());

    assertThat(result.displayContentCount()).isEqualTo(1);
    assertThat(result.displayArtworkCount()).isEqualTo(1);
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.PUBLISHED);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.PUBLISHED);
  }

  @Test
  void publishForDisplayKeepsOnExhibitionChildrenDraftWhenDisplayHasNotStarted() {
    Display display =
        display(
            LocalDate.of(2026, 8, 3),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    addDraftContent(display);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_008L;
    insertArtwork(artworkId, savedDisplay.getId(), "개막 공개 예정 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result =
        publicationService.publishForDisplay(savedDisplay.getId());

    assertThat(result.displayContentCount()).isZero();
    assertThat(result.displayArtworkCount()).isZero();
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.DRAFT);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.DRAFT);
  }

  @Test
  void publishForDisplayPublishesOnExhibitionChildrenWhenDisplayStarted() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    addDraftContent(display);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_009L;
    insertArtwork(artworkId, savedDisplay.getId(), "개막 공개 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result =
        publicationService.publishForDisplay(savedDisplay.getId());

    assertThat(result.displayContentCount()).isEqualTo(1);
    assertThat(result.displayArtworkCount()).isEqualTo(1);
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.PUBLISHED);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.PUBLISHED);
  }

  @Test
  void publishForDisplayAppliesContentAndArtworkPoliciesIndependently() {
    Display display =
        display(
            LocalDate.of(2026, 8, 3),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    addDraftContent(display);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_010L;
    insertArtwork(artworkId, savedDisplay.getId(), "즉시 공개 작품", DisplayArtworkStatus.DRAFT);

    DisplayContentPublicationResult result =
        publicationService.publishForDisplay(savedDisplay.getId());

    assertThat(result.displayContentCount()).isZero();
    assertThat(result.displayArtworkCount()).isEqualTo(1);
    assertThat(contentStatus(savedDisplay.getId())).isEqualTo(DisplayContentStatus.DRAFT);
    assertThat(artworkStatus(artworkId)).isEqualTo(DisplayArtworkStatus.PUBLISHED);
  }

  @Test
  void displayDetailReturnsPublishedContentsOnly() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-published.jpg",
                    0,
                    DisplayContentStatus.PUBLISHED),
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    1,
                    DisplayContentStatus.DRAFT))));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);

    var result = getDisplayDetailService.getDisplayDetail(savedDisplay.getId(), null);

    assertThat(result.contentCategories()).hasSize(1);
    assertThat(result.contentCategories().getFirst().contents()).hasSize(1);
    assertThat(result.contentCategories().getFirst().contents().getFirst().imageUrl())
        .isEqualTo("https://cdn.displayu.com/display/content-published.jpg");
  }

  @Test
  void displayDetailIncludesDraftContentsForDisplayOwner() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-published.jpg",
                    0,
                    DisplayContentStatus.PUBLISHED),
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    1,
                    DisplayContentStatus.DRAFT))));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);

    var result = getDisplayDetailService.getDisplayDetail(savedDisplay.getId(), 1L);

    assertThat(result.contentCategories()).hasSize(1);
    assertThat(result.contentCategories().getFirst().contents())
        .extracting(content -> content.imageUrl())
        .containsExactly(
            "https://cdn.displayu.com/display/content-published.jpg",
            "https://cdn.displayu.com/display/content-draft.jpg");
  }

  @Test
  void displayArtworkQueriesReturnPublishedArtworksOnly() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long publishedArtworkId = 10_003L;
    Long draftArtworkId = 10_004L;
    insertArtwork(
        publishedArtworkId, savedDisplay.getId(), "공개 작품", DisplayArtworkStatus.PUBLISHED);
    insertArtwork(draftArtworkId, savedDisplay.getId(), "초안 작품", DisplayArtworkStatus.DRAFT);

    var listResult = displayArtworkQueryService.getArtworksByDisplayId(savedDisplay.getId(), null);

    assertThat(listResult.artworks()).hasSize(1);
    assertThat(listResult.artworks().getFirst().artworkId()).isEqualTo(publishedArtworkId);
    assertThat(displayArtworkQueryService.getDisplayArtworkFullDetail(publishedArtworkId, null))
        .isNotNull();
    assertThatThrownBy(
            () -> displayArtworkQueryService.getDisplayArtworkFullDetail(draftArtworkId, null))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND.getMessage());
  }

  @Test
  void displayArtworkQueriesRejectPublishedArtworkWhenDisplayIsDraft() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_005L;
    insertArtwork(artworkId, savedDisplay.getId(), "전시 미공개 작품", DisplayArtworkStatus.PUBLISHED);

    assertThatThrownBy(
            () -> displayArtworkQueryService.getArtworksByDisplayId(savedDisplay.getId(), null))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND.getMessage());
    assertThatThrownBy(
            () -> displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, null))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(DisplayArtworkErrorCode.DISPLAY_ARTWORK_NOT_FOUND.getMessage());
  }

  @Test
  void displayArtworkQueriesIncludeDraftArtworksForDisplayOwner() {
    Display display =
        display(
            LocalDate.of(2026, 8, 1),
            ContentOpenPolicy.ON_EXHIBITION,
            ContentOpenPolicy.ON_EXHIBITION);
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long artworkId = 10_006L;
    insertArtwork(artworkId, savedDisplay.getId(), "초안 작품", DisplayArtworkStatus.DRAFT);

    var listResult = displayArtworkQueryService.getArtworksByDisplayId(savedDisplay.getId(), 1L);
    var detailResult = displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, 1L);

    assertThat(listResult.artworks()).hasSize(1);
    assertThat(listResult.artworks().getFirst().artworkId()).isEqualTo(artworkId);
    assertThat(detailResult.artworkId()).isEqualTo(artworkId);
  }

  private static Display display(
      LocalDate startDate,
      ContentOpenPolicy artworkContentOpen,
      ContentOpenPolicy exhibitionContentOpen) {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "전시 부제",
        "전시 설명",
        new DisplayLocation("중앙대학교 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
        "",
        "전시 유의사항",
        "중앙대학교",
        "디자인학부",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        DisplayRegion.SEOUL,
        new DisplayPeriod(
            startDate, startDate.plusDays(7), LocalTime.of(10, 0), LocalTime.of(18, 0)),
        artworkContentOpen,
        exhibitionContentOpen);
  }

  private static void addDraftContent(Display display) {
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시 소개",
            "전시 소개 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-draft.jpg",
                    0,
                    DisplayContentStatus.DRAFT))));
  }

  private void insertArtwork(
      Long artworkId, Long displayId, String artworkName, DisplayArtworkStatus status) {
    jdbcTemplate.update(
        """
        INSERT INTO display_artwork (
          display_artwork_id,
          artwork_name,
          content,
          type,
          production_year,
          material_media,
          size,
          point,
          work_sort_order,
          registered_by_user_id,
          status,
          created_at,
          updated_at,
          display_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)
        """,
        artworkId,
        artworkName,
        "작품 설명입니다.",
        "DESIGN",
        2026,
        "디지털 프린트",
        "100x100cm",
        "작품 포인트입니다.",
        0,
        1L,
        status.name(),
        displayId);
  }

  private DisplayArtworkStatus artworkStatus(Long artworkId) {
    String status =
        jdbcTemplate.queryForObject(
            "SELECT status FROM display_artwork WHERE display_artwork_id = ?",
            String.class,
            artworkId);
    return DisplayArtworkStatus.valueOf(status);
  }

  private DisplayContentStatus contentStatus(Long displayId) {
    String status =
        jdbcTemplate.queryForObject(
            """
            SELECT content.status
            FROM display_content content
            JOIN display_content_category category ON category.category_id = content.category_id
            WHERE category.display_id = ?
            """,
            String.class,
            displayId);
    return DisplayContentStatus.valueOf(status);
  }

  @TestConfiguration
  static class FixedClockConfig {

    @Bean
    @Primary
    Clock fixedClock() {
      return Clock.fixed(Instant.parse("2026-08-01T15:00:00Z"), ZoneId.of("Asia/Seoul"));
    }
  }
}
