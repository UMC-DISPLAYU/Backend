package com.example.demo.domain.displayartwork.infrastructure.persistence;

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
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class ArtworkDiscoveryControllerTest {
  private static final Long DISPLAY_OWNER = 1L;
  @Autowired private EntityManager entityManager;
  @Autowired private org.springframework.test.web.servlet.MockMvc mvc;
  private long nextCreatorId = 80001L;
  @Autowired private java.time.Clock clock;

  @Test
  void searchMatchesTitleAndAllArtistsButNotQaOnlyWithoutDuplicates() throws Exception {
    Display display = persistedDisplay();
    DisplayArtwork titled = persistArtwork(display, "Needle%_", 0);
    DisplayArtwork authored = persistArtwork(display, "다른 작품", 1);
    persistCreator(authored.getId(), "Needle 공동", CreatorRole.CO_AUTHOR, 20L);
    persistCreator(authored.getId(), "Needle 이름", CreatorRole.CO_AUTHOR, null);
    DisplayArtwork qa = persistArtwork(display, "QnA 전용", 2);
    persistCreator(qa.getId(), "Needle 담당", CreatorRole.QA_ONLY, 30L);
    search(" needle ", null, null, 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(2))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(authored.getId()));
    search("%_", null, null, 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(1))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(titled.getId()));
  }

  @Test
  void searchUsesParentTypeAndPeriodAndExcludesUnpublishedOrDeletedParentsAndWorks()
      throws Exception {
    Display display = persistedDisplay();
    display.changePeriod(
        new DisplayPeriod(
            java.time.LocalDate.now(clock),
            java.time.LocalDate.now(clock).plusDays(2),
            LocalTime.NOON,
            LocalTime.of(18, 0)));
    entityManager.flush();
    DisplayArtwork visible = persistArtwork(display, "공개", 0);
    search(null, "GRADUATION", "ONGOING", 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(visible.getId()));
    search(null, "GRADUATION", "UPCOMING", 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(0));
    org.springframework.test.util.ReflectionTestUtils.setField(
        visible,
        "status",
        com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.DRAFT);
    entityManager.flush();
    search(null, null, null, 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(0));
    org.springframework.test.util.ReflectionTestUtils.setField(
        visible,
        "status",
        com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.PUBLISHED);
    org.springframework.test.util.ReflectionTestUtils.setField(
        display, "deletedAt", java.time.LocalDateTime.now());
    entityManager.flush();
    search(null, null, null, 0, 20)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(0));
  }

  @Test
  void searchPagesWithoutSkippingOrRepeatingBoundaryRows() throws Exception {
    Display display = persistedDisplay();
    var a = persistArtwork(display, "A", 0);
    var b = persistArtwork(display, "B", 1);
    var c = persistArtwork(display, "C", 2);
    search(null, null, null, 0, 2)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(2))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(c.getId()))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[1].artworkId")
                .value(b.getId()))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.isLast")
                .value(false));
    search(null, null, null, 1, 2)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(1))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(a.getId()))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.isLast")
                .value(true));
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                    "/api/v1/artworks/search")
                .param("size", "101"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status()
                .isBadRequest());
  }

  @Test
  void searchFiltersBySingleFieldIncludingLegacyAliasRows() throws Exception {
    Display display = persistedDisplay();
    DisplayArtwork painting = persistArtwork(display, "회화 작품", List.of(ArtworkType.PAINTING), 0);
    DisplayArtwork video = persistArtwork(display, "영상 작품", List.of(ArtworkType.VIDEO), 1);
    DisplayArtwork legacyVideo =
        persistArtwork(display, "예전 영상 작품", List.of(ArtworkType.PHOTOGRAPHY), 2);
    // 분야 개편 전 별칭 값(MEDIA)으로 저장된 행을 재현한다.
    entityManager
        .createQuery(
            "UPDATE ArtworkField f SET f.field = :legacy WHERE f.displayArtwork.id = :artworkId")
        .setParameter("legacy", ArtworkType.MEDIA)
        .setParameter("artworkId", legacyVideo.getId())
        .executeUpdate();
    entityManager.clear();

    searchWith("field", "VIDEO")
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[*].artworkId")
                .value(
                    org.hamcrest.Matchers.contains(
                        legacyVideo.getId().intValue(), video.getId().intValue())));
    searchWith("field", "PAINTING")
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[*].artworkId")
                .value(org.hamcrest.Matchers.contains(painting.getId().intValue())));
  }

  @Test
  void searchSortsByActiveLikeCountThenIdWhenSortIsLikes() throws Exception {
    Display display = persistedDisplay();
    DisplayArtwork twoLikes = persistArtwork(display, "좋아요 2", 0);
    DisplayArtwork oneLike = persistArtwork(display, "좋아요 1", 1);
    DisplayArtwork onlyCanceledLikes = persistArtwork(display, "취소된 좋아요만", 2);
    DisplayArtwork oneLikeNewer = persistArtwork(display, "좋아요 1 최신", 3);
    persistLike(twoLikes.getId(), 101L, false);
    persistLike(twoLikes.getId(), 102L, false);
    persistLike(oneLike.getId(), 101L, false);
    persistLike(onlyCanceledLikes.getId(), 101L, true);
    persistLike(onlyCanceledLikes.getId(), 102L, true);
    persistLike(onlyCanceledLikes.getId(), 103L, true);
    persistLike(oneLikeNewer.getId(), 102L, false);

    searchWith("sort", "LIKES")
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[*].artworkId")
                .value(
                    org.hamcrest.Matchers.contains(
                        twoLikes.getId().intValue(),
                        oneLikeNewer.getId().intValue(),
                        oneLike.getId().intValue(),
                        onlyCanceledLikes.getId().intValue())));
    searchWith("sort", "LATEST")
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[*].artworkId")
                .value(
                    org.hamcrest.Matchers.contains(
                        oneLikeNewer.getId().intValue(),
                        onlyCanceledLikes.getId().intValue(),
                        oneLike.getId().intValue(),
                        twoLikes.getId().intValue())));
  }

  private org.springframework.test.web.servlet.ResultActions searchWith(String name, String value)
      throws Exception {
    return mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                    "/api/v1/artworks/search")
                .param(name, value))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
  }

  private void persistLike(Long artworkId, Long userId, boolean canceled) {
    var like =
        com.example.demo.domain.displayartwork.domain.entity.DisplayArtworkLike.create(
            artworkId, userId);
    if (canceled) like.cancel();
    entityManager.persist(like);
    entityManager.flush();
  }

  private org.springframework.test.web.servlet.ResultActions search(
      String word, String type, String status, int page, int size) throws Exception {
    var request =
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                "/api/v1/artworks/search")
            .param("page", Integer.toString(page))
            .param("size", Integer.toString(size));
    if (word != null) request.param("searchWord", word);
    if (type != null) request.param("type", type);
    if (status != null) request.param("status", status);
    return mvc.perform(request)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
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
