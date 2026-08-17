package com.example.demo.domain.displayartwork.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 작품 조회 쿼리 중 조건이 까다로운 두 가지를 검증한다.
 *
 * <p>{@code findAllByParticipantUserId} — Creator는 작가(LEAD_ARTIST/CO_AUTHOR) 외에 QnA 답변만 담당하는 전시
 * 대표자(QA_ONLY)로도 생성되므로, role을 구분하지 않으면 대리 등록해 준 남의 작품이 대표자의 작가 프로필에 노출된다.
 *
 * <p>{@code findPreview} — 분야 필터는 다중 선택이라 IN 조건을 쓴다. 필터를 하나도 고르지 않으면 빈 목록이 IN에 들어가 쿼리가 깨지므로, 그 경우는
 * 플래그로 조건을 건너뛴다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpringDataDisplayArtworkJpaRepositoryTest {

  private static final Long DISPLAY_OWNER = 1L;
  private static final Long PARTICIPANT = 10L;

  @Autowired private SpringDataDisplayArtworkJpaRepository jpaRepository;

  @Autowired private SpringDataCreatorJpaRepository creatorJpaRepository;

  @Autowired private EntityManager entityManager;

  private long nextCreatorId = 1L;

  @Test
  void findAllByParticipantUserIdIncludesLeadArtist() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "대표 작가로 참여한 작품", 0);
    persistCreator(artwork.getId(), "참여 작가", CreatorRole.LEAD_ARTIST, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(artwork.getId());
  }

  @Test
  void findAllByParticipantUserIdIncludesCoAuthor() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "공동 작업자로 참여한 작품", 0);
    persistCreator(artwork.getId(), "대표 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(artwork.getId(), "참여 작가", CreatorRole.CO_AUTHOR, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(artwork.getId());
  }

  @Test
  void findAllByParticipantUserIdExcludesQaOnlyHandler() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "대리 등록해 준 남의 작품", 0);
    // 계정 없는 작가를 대리 등록하면 대표 작가의 userId는 null이고, 담당자 지정을 위해 대표자에게 QA_ONLY가 생긴다.
    persistCreator(artwork.getId(), "계정 없는 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(artwork.getId(), "QnA 담당자", CreatorRole.QA_ONLY, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT)).isEmpty();
  }

  @Test
  void findAllByParticipantUserIdReturnsOnlyArtworksParticipatedAsArtist() {
    Display display = persistedDisplay();
    DisplayArtwork own = persistArtwork(display, "본인이 작가인 작품", 0);
    DisplayArtwork proxied = persistArtwork(display, "대리 등록만 해 준 작품", 1);
    persistCreator(own.getId(), "참여 작가", CreatorRole.LEAD_ARTIST, PARTICIPANT);
    persistCreator(proxied.getId(), "계정 없는 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(proxied.getId(), "QnA 담당자", CreatorRole.QA_ONLY, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(own.getId());
  }

  @Test
  void findPreviewReturnsAllArtworksWhenFieldFilterIsIgnored() {
    Display display = persistedDisplay();
    persistArtwork(display, "회화 작품", List.of(ArtworkType.PAINTING), 0);
    persistArtwork(display, "디자인 작품", List.of(ArtworkType.DESIGN), 1);

    List<DisplayArtwork> found =
        jpaRepository.findPreview(
            false, true, List.of(ArtworkType.PAINTING), 1, null, PageRequest.of(0, 20));

    assertThat(found)
        .extracting(DisplayArtwork::getArtworkName)
        .containsExactlyInAnyOrder("회화 작품", "디자인 작품");
  }

  @Test
  void findPreviewReturnsArtworksHavingSelectedFieldWhenSingleFieldGiven() {
    Display display = persistedDisplay();
    persistArtwork(display, "회화만", List.of(ArtworkType.PAINTING), 0);
    persistArtwork(display, "회화와 사진", List.of(ArtworkType.PAINTING, ArtworkType.PHOTOGRAPHY), 1);
    persistArtwork(display, "디자인만", List.of(ArtworkType.DESIGN), 2);

    List<DisplayArtwork> found =
        jpaRepository.findPreview(
            false, false, List.of(ArtworkType.PAINTING), 1, null, PageRequest.of(0, 20));

    // 분야를 2개 가진 작품도 그중 하나가 회화면 포함된다.
    assertThat(found)
        .extracting(DisplayArtwork::getArtworkName)
        .containsExactlyInAnyOrder("회화만", "회화와 사진");
  }

  @Test
  void findPreviewReturnsOnlyArtworksHavingAllSelectedFields() {
    Display display = persistedDisplay();
    persistArtwork(display, "회화만", List.of(ArtworkType.PAINTING), 0);
    persistArtwork(display, "사진만", List.of(ArtworkType.PHOTOGRAPHY), 1);
    persistArtwork(display, "회화와 사진", List.of(ArtworkType.PAINTING, ArtworkType.PHOTOGRAPHY), 2);

    List<DisplayArtwork> found =
        jpaRepository.findPreview(
            false,
            false,
            List.of(ArtworkType.PAINTING, ArtworkType.PHOTOGRAPHY),
            2,
            null,
            PageRequest.of(0, 20));

    // AND 조건이므로 두 분야를 모두 가진 작품만 나온다.
    assertThat(found).extracting(DisplayArtwork::getArtworkName).containsExactly("회화와 사진");
  }

  @Test
  void findPreviewReturnsEmptyWhenNoArtworkHasAllSelectedFields() {
    Display display = persistedDisplay();
    persistArtwork(display, "회화만", List.of(ArtworkType.PAINTING), 0);
    persistArtwork(display, "사진만", List.of(ArtworkType.PHOTOGRAPHY), 1);

    List<DisplayArtwork> found =
        jpaRepository.findPreview(
            false,
            false,
            List.of(ArtworkType.PAINTING, ArtworkType.PHOTOGRAPHY),
            2,
            null,
            PageRequest.of(0, 20));

    assertThat(found).isEmpty();
  }

  @Test
  void keepsExistingFieldRowWhenArtworkIsUpdatedWithSameField() {
    Display display = persistedDisplay();
    DisplayArtwork saved =
        jpaRepository.saveAndFlush(buildArtwork(display, "작품", List.of(ArtworkType.PAINTING), 0));
    Long fieldRowId = saved.getFields().getFirst().getId();
    entityManager.clear();

    DisplayArtwork loaded = jpaRepository.findById(saved.getId()).orElseThrow();
    loaded.changeContent("수정된 작품", "설명", List.of(ArtworkType.PAINTING), 2026, "재료", "크기", "포인트");
    jpaRepository.saveAndFlush(loaded);
    entityManager.clear();

    DisplayArtwork reloaded = jpaRepository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFieldTypes()).containsExactly(ArtworkType.PAINTING);
    // 유지되는 분야는 기존 행을 그대로 둔다. 지우고 다시 넣으면 유니크 제약에 걸려 저장 자체가 실패한다.
    assertThat(reloaded.getFields().getFirst().getId()).isEqualTo(fieldRowId);
  }

  @Test
  void replacesOnlyChangedFieldWhenArtworkIsUpdated() {
    Display display = persistedDisplay();
    DisplayArtwork saved =
        jpaRepository.saveAndFlush(
            buildArtwork(display, "작품", List.of(ArtworkType.PAINTING, ArtworkType.DESIGN), 0));
    Long keptRowId =
        saved.getFields().stream()
            .filter(field -> field.getField() == ArtworkType.PAINTING)
            .findFirst()
            .orElseThrow()
            .getId();
    entityManager.clear();

    DisplayArtwork loaded = jpaRepository.findById(saved.getId()).orElseThrow();
    loaded.changeContent(
        "수정된 작품", "설명", List.of(ArtworkType.PAINTING, ArtworkType.MEDIA), 2026, "재료", "크기", "포인트");
    jpaRepository.saveAndFlush(loaded);
    entityManager.clear();

    DisplayArtwork reloaded = jpaRepository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFieldTypes())
        .containsExactlyInAnyOrder(ArtworkType.PAINTING, ArtworkType.MEDIA);
    assertThat(
            reloaded.getFields().stream()
                .filter(field -> field.getField() == ArtworkType.PAINTING)
                .findFirst()
                .orElseThrow()
                .getId())
        .isEqualTo(keptRowId);
  }

  @Test
  void renameCreatorNamesInDisplayUpdatesOnlyCreatorsUsingPreviousName() {
    Display display = persistedDisplay();
    DisplayArtwork usingDefault = persistArtwork(display, "기본 이름을 쓰던 작품", 0);
    DisplayArtwork renamed = persistArtwork(display, "작품별로 이름을 바꾼 작품", 1);
    persistCreator(usingDefault.getId(), "beanie", CreatorRole.LEAD_ARTIST, PARTICIPANT);
    persistCreator(renamed.getId(), "스튜디오 접다", CreatorRole.LEAD_ARTIST, PARTICIPANT);

    int updated =
        creatorJpaRepository.renameCreatorNamesInDisplay(
            display.getId(), PARTICIPANT, "beanie", "세현");

    assertThat(updated).isEqualTo(1);
    assertThat(creatorNames(usingDefault.getId())).containsExactly("세현");
    // 작품별로 지정한 표기명은 전시 작가명이 바뀌어도 그대로 둔다.
    assertThat(creatorNames(renamed.getId())).containsExactly("스튜디오 접다");
  }

  @Test
  void renameCreatorNamesInDisplayDoesNotTouchOtherUsersOrDisplays() {
    Display display = persistedDisplay();
    Display otherDisplay = persistedDisplay();
    DisplayArtwork mine = persistArtwork(display, "내 작품", 0);
    DisplayArtwork someoneElse = persistArtwork(display, "동명이인 작품", 1);
    DisplayArtwork otherDisplayArtwork = persistArtwork(otherDisplay, "다른 전시의 내 작품", 0);
    persistCreator(mine.getId(), "beanie", CreatorRole.LEAD_ARTIST, PARTICIPANT);
    persistCreator(someoneElse.getId(), "beanie", CreatorRole.LEAD_ARTIST, DISPLAY_OWNER);
    persistCreator(otherDisplayArtwork.getId(), "beanie", CreatorRole.LEAD_ARTIST, PARTICIPANT);

    int updated =
        creatorJpaRepository.renameCreatorNamesInDisplay(
            display.getId(), PARTICIPANT, "beanie", "세현");

    assertThat(updated).isEqualTo(1);
    assertThat(creatorNames(mine.getId())).containsExactly("세현");
    // 이름이 같아도 다른 사용자, 다른 전시는 건드리지 않는다.
    assertThat(creatorNames(someoneElse.getId())).containsExactly("beanie");
    assertThat(creatorNames(otherDisplayArtwork.getId())).containsExactly("beanie");
  }

  @Test
  void renameCreatorNamesInDisplayUpdatesNothingWhenNameIsUnused() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "작품", 0);
    persistCreator(artwork.getId(), "beanie", CreatorRole.LEAD_ARTIST, PARTICIPANT);

    int updated =
        creatorJpaRepository.renameCreatorNamesInDisplay(
            display.getId(), PARTICIPANT, "쓰이지 않는 이름", "세현");

    assertThat(updated).isZero();
    assertThat(creatorNames(artwork.getId())).containsExactly("beanie");
  }

  private List<String> creatorNames(Long artworkId) {
    return creatorJpaRepository.findByDisplayArtworkId(artworkId).stream()
        .map(Creator::getCreatorName)
        .toList();
  }

  private Display persistedDisplay() {
    Display display =
        Display.create(
            new UserId(DISPLAY_OWNER),
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
    // 조회 조건이 전시/작품 모두 PUBLISHED를 요구한다.
    display.publish();
    entityManager.persist(display);
    entityManager.flush();
    return display;
  }

  private DisplayArtwork persistArtwork(Display display, String artworkName, int workSortOrder) {
    return persistArtwork(display, artworkName, List.of(ArtworkType.PAINTING), workSortOrder);
  }

  private DisplayArtwork persistArtwork(
      Display display, String artworkName, List<ArtworkType> types, int workSortOrder) {
    DisplayArtwork artwork = buildArtwork(display, artworkName, types, workSortOrder);
    entityManager.persist(artwork);
    entityManager.flush();
    return artwork;
  }

  private DisplayArtwork buildArtwork(
      Display display, String artworkName, List<ArtworkType> types, int workSortOrder) {
    return DisplayArtwork.create(
        display,
        artworkName,
        "content",
        types,
        2026,
        "Oil on canvas",
        "72.7 x 90.9 cm",
        "point",
        workSortOrder,
        DISPLAY_OWNER,
        List.of(
            new ArtworkImage(
                null,
                "https://cdn.displayu.com/artworks/main.png",
                true,
                ArtworkImageType.ARTWORK,
                0,
                "대표 이미지",
                1200,
                1600)));
  }

  /**
   * 계정 없는 작가를 대리 등록하는 경우를 재현해야 하므로 userId에 null을 받는다.
   *
   * <p>Creator 테이블은 artworkcommunication의 참조 엔티티도 매핑하는데 그쪽에 식별자 생성 전략이 없어, H2 스키마의 creator_id에
   * identity가 붙지 않는다. 그래서 JPA persist 대신 ID를 직접 지정해 넣는다. 운영 MySQL은 Flyway가 auto_increment로 만들므로 영향이
   * 없다.
   */
  private void persistCreator(Long artworkId, String creatorName, CreatorRole role, Long userId) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO creator
              (creator_id, display_artwork_id, creator_name, is_contact, is_leader, role, user_id)
            VALUES (?, ?, ?, FALSE, ?, ?, ?)
            """)
        .setParameter(1, nextCreatorId++)
        .setParameter(2, artworkId)
        .setParameter(3, creatorName)
        .setParameter(4, role == CreatorRole.LEAD_ARTIST)
        .setParameter(5, role.name())
        .setParameter(6, userId)
        .executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }
}
