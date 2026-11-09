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
class DisplayArtworkPreviewArtistsTest {
  private static final Long DISPLAY_OWNER = 1L;
  @Autowired private EntityManager entityManager;
  @Autowired private org.springframework.test.web.servlet.MockMvc mvc;
  private long nextCreatorId = 70001L;

  @Test
  void previewReturnsAllArtistsAndExcludesQaOnly() throws Exception {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "공동 작품", 0);
    persistCreator(artwork.getId(), "동명이인", CreatorRole.CO_AUTHOR, null);
    persistCreator(artwork.getId(), "대표", CreatorRole.LEAD_ARTIST, 10L);
    persistCreator(artwork.getId(), "동명이인", CreatorRole.CO_AUTHOR, 20L);
    persistCreator(artwork.getId(), "담당자", CreatorRole.QA_ONLY, 30L);
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                    "/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artists.length()")
                .value(3))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artists[0].userId")
                .value(10))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artists[0].name")
                .value("대표"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artists[1].userId")
                .isEmpty())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artists[2].userId")
                .value(20))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.artworks[0].artistUserId")
                .value(10));
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
