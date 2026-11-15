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

@org.springframework.boot.test.context.SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:recommendations;MODE=MySQL;DATABASE_TO_UPPER=FALSE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
    })
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class ArtworkRecommendationControllerTest {
  private static final Long DISPLAY_OWNER = 1L;
  @Autowired private EntityManager entityManager;
  @Autowired private org.springframework.test.web.servlet.MockMvc mvc;
  private long nextCreatorId = 70001L;

  @Test
  void roundRobinRanksDisplaysThenWorksByFirstPublicationRatherThanCreation() throws Exception {
    var a = persistedDisplay();
    var b = persistedDisplay();
    var c = persistedDisplay();
    var a1 = published(a, "A1", 30);
    var a2 = published(a, "A2", 15);
    var a3 = published(a, "A3", 1);
    var b1 = published(b, "B1", 25);
    var c1 = published(c, "C1", 20);
    var c2 = published(c, "C2", 5);
    var ids = List.of(a1.getId(), b1.getId(), c1.getId(), a2.getId(), c2.getId(), a3.getId());
    var result = preview(null, null, null);
    result
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(6))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.size")
                .value(10))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.isLast")
                .value(true));
    for (int i = 0; i < ids.size(); i++)
      result.andExpect(
          org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                  "$.success.data.artworks[" + i + "].artworkId")
              .value(ids.get(i)));
    preview(null, null, "1")
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(0));
  }

  @Test
  void filtersBeforeRankingAndExcludesDeletedParentsDraftAndDeletedArtworks() throws Exception {
    var a = persistedDisplay();
    var b = persistedDisplay();
    var nonmatching = published(a, "A newest", 30);
    nonmatching.replaceFields(List.of(ArtworkType.BRANDING));
    var a1 = published(a, "A matching", 5);
    var b1 = published(b, "B matching", 20);
    var draft = published(b, "draft", 40);
    org.springframework.test.util.ReflectionTestUtils.setField(
        draft,
        "status",
        com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.DRAFT);
    var removed = published(b, "deleted", 50);
    org.springframework.test.util.ReflectionTestUtils.setField(
        removed, "deletedAt", java.time.LocalDateTime.now());
    var deletedDisplay = persistedDisplay();
    published(deletedDisplay, "deleted parent", 60);
    org.springframework.test.util.ReflectionTestUtils.setField(
        deletedDisplay, "deletedAt", java.time.LocalDateTime.now());
    entityManager.flush();
    preview("PAINTING", null, null)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(2))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(b1.getId()))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[1].artworkId")
                .value(a1.getId()));
    preview(null, "unknown", null)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(0));
  }

  @Test
  void capsAtTenForOneDisplayAndMoreThanTenDisplays() throws Exception {
    var first = persistedDisplay();
    for (int i = 1; i <= 12; i++) published(first, "one" + i, i);
    preview(null, null, null)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(10))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[9].artworkName")
                .value("one3"));
    for (int i = 1; i <= 12; i++) published(persistedDisplay(), "many" + i, 20 + i);
    preview(null, null, null)
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(10))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkName")
                .value("many12"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[9].artworkName")
                .value("many3"));
  }

  @Test
  void graduationPagesKeepAdjacentRowsAndPublishedTimestampNeverChanges() throws Exception {
    var display = persistedDisplay();
    var a = published(display, "A", 1);
    var b = published(display, "B", 2);
    var c = published(display, "C", 3);
    var first = a.getFirstPublishedAt();
    a.publish(first.plusDays(9));
    entityManager.flush();
    entityManager.clear();
    org.assertj.core.api.Assertions.assertThat(
            entityManager.find(DisplayArtwork.class, a.getId()).getFirstPublishedAt())
        .isEqualTo(first);
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                    "/api/v1/artworks/preview")
                .param("type", "GRADUATION")
                .param("page", "0")
                .param("size", "2"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(c.getId()))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[1].artworkId")
                .value(b.getId()));
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                    "/api/v1/artworks/preview")
                .param("type", "GRADUATION")
                .param("page", "1")
                .param("size", "2"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks.length()")
                .value(1))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artworkId")
                .value(a.getId()));
  }

  @Test
  void tiesHaveStableDisplayAndArtworkIdOrder() throws Exception {
    var a = persistedDisplay();
    var b = persistedDisplay();
    var a1 = published(a, "A1", 10);
    var a2 = published(a, "A2", 10);
    var b1 = published(b, "B1", 10);
    var b2 = published(b, "B2", 10);
    var result = preview(null, null, null);
    var ids = List.of(b2.getId(), a2.getId(), b1.getId(), a1.getId());
    for (int i = 0; i < ids.size(); i++)
      result.andExpect(
          org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                  "$.success.data.artworks[" + i + "].artworkId")
              .value(ids.get(i)));
  }

  private DisplayArtwork published(Display display, String name, int minute) {
    var artwork = buildArtwork(display, name, List.of(ArtworkType.PAINTING), 0);
    artwork.publish(java.time.LocalDateTime.of(2026, 1, 1, 0, 0).plusMinutes(minute));
    entityManager.persist(artwork);
    entityManager.flush();
    return artwork;
  }

  private org.springframework.test.web.servlet.ResultActions preview(
      String field, String school, String page) throws Exception {
    var request =
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                "/api/v1/artworks/preview")
            .param("type", "RECOMMEND");
    if (field != null) request.param("field", field);
    if (school != null) request.param("school", school);
    if (page != null) request.param("page", page);
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
            INSERT INTO Creator
              (creatorId, displayArtworkId, creatorName, isContact, isLeader, role, userId)
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
