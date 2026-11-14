package com.example.demo.domain.displayartwork.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryRepository;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import com.example.demo.global.file.domain.MediaContent;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:artwork_media_test;MODE=MySQL;DATABASE_TO_UPPER=FALSE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true",
      "spring.jpa.properties.hibernate.hbm2ddl.import_files=media-test-identities.sql"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayArtworkMediaControllerTest {
  private static final String VIDEO = "https://cdn.example/artwork/movie.mp4";
  private static final String THUMBNAIL = "https://cdn.example/artwork/thumbnail.png";
  private static final String IMAGE = "https://cdn.example/artwork/image.png";
  @Autowired private MockMvc mvc;
  @Autowired private JwtFactory jwtFactory;
  @Autowired private EntityManager entityManager;
  @Autowired private ArtworkSummaryQueryRepository summaries;
  @MockitoBean private MediaStorageVerifier storageVerifier;
  private Long ownerId;
  private Long displayId;

  @BeforeEach
  void fixture() {
    ownerId = 262L;
    entityManager
        .createNativeQuery(
            """
            INSERT INTO `User` (userId, createdAt, updatedAt, provider, providerId, role,
              name, nickname, socialEmail, isVerified)
            VALUES (:id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Google', 'artwork-media-owner',
              'USER', '미디어 작가', '미디어 작가', 'media@example.com', TRUE)
            """)
        .setParameter("id", ownerId)
        .executeUpdate();
    Display display =
        Display.create(
            new UserId(ownerId),
            "영상 전시",
            "https://cdn.example/display/poster.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.55"), new BigDecimal("126.92")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.publish();
    entityManager.persist(display);
    entityManager.flush();
    displayId = display.getId();
    entityManager.clear();
  }

  @Test
  void videoRoundTripsThroughCreateDetailEditAndUpdate() throws Exception {
    create(videoJson())
        .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(VIDEO))
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("VIDEO"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").value(THUMBNAIL));
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    verify(storageVerifier).verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.ARTWORK);
    for (String suffix : List.of("", "/edit")) {
      mvc.perform(
              get("/api/v1/artworks/" + id + suffix)
                  .header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(VIDEO))
          .andExpect(jsonPath("$.success.data.images[0].mediaType").value("VIDEO"))
          .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").value(THUMBNAIL))
          .andExpect(jsonPath("$.success.data.images[0].width").value(800));
    }
    update(id, legacyImageJson())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").isEmpty());
    entityManager.flush();
    entityManager.clear();
    mvc.perform(
            get("/api/v1/artworks/{id}/edit", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(IMAGE));
    update(id, videoJson())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("VIDEO"));
  }

  @Test
  void allCardQueriesAndArchiveSummaryUseThumbnail() throws Exception {
    create(videoJson());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    mvc.perform(get("/api/v1/artworks").param("displayId", displayId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkImageUrl").value(THUMBNAIL))
        .andExpect(jsonPath("$.success.data.artworks[0].artworkVideoUrl").value(VIDEO))
        .andExpect(jsonPath("$.success.data.artworks[0].imageWidth").value(800));
    mvc.perform(get("/api/v1/artworks").param("userId", ownerId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkImageUrl").value(THUMBNAIL));
    mvc.perform(
            get("/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkImageUrl").value(THUMBNAIL));
    assertThat(summaries.findByDisplayArtworkIdIn(List.of(id)))
        .singleElement()
        .satisfies(summary -> assertThat(summary.artworkImageUrl()).isEqualTo(THUMBNAIL));
  }

  @Test
  void legacyImageJsonStillCreatesAndProjectsAnImage() throws Exception {
    create(legacyImageJson())
        .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(IMAGE))
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").isEmpty());
    entityManager.flush();
    entityManager.clear();
    mvc.perform(get("/api/v1/artworks").param("displayId", displayId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkImageUrl").value(IMAGE))
        .andExpect(jsonPath("$.success.data.artworks[0].artworkVideoUrl").isEmpty());
  }

  @ParameterizedTest
  @MethodSource("invalidImages")
  void invalidMediaReplacementReturns400AndPreservesOriginal(String images) throws Exception {
    create(legacyImageJson());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    update(id, images).andExpect(status().isBadRequest());
    entityManager.clear();
    DisplayArtwork original = entityManager.find(DisplayArtwork.class, id);
    assertThat(original.getImages())
        .singleElement()
        .satisfies(image -> assertThat(image.getImageUrl()).isEqualTo(IMAGE));
  }

  @Test
  void failedStorageVerificationPreservesOriginal() throws Exception {
    create(legacyImageJson());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    doThrow(new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE))
        .when(storageVerifier)
        .verifyVideo(eq(MediaContent.video(VIDEO, THUMBNAIL)), eq(MediaUsage.ARTWORK));
    update(id, videoJson()).andExpect(status().isBadRequest());
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, id).getImages())
        .singleElement()
        .satisfies(image -> assertThat(image.getImageUrl()).isEqualTo(IMAGE));
  }

  @Test
  void unauthorizedCreateAndUpdateNeverInspectStorage() throws Exception {
    create(legacyImageJson());
    Long id = artworkId();
    clearInvocations(storageVerifier);
    mvc.perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId + 1000))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(videoJson())))
        .andExpect(status().isForbidden());
    mvc.perform(
            patch("/api/v1/artworks/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId + 1000))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(videoJson())))
        .andExpect(status().isForbidden());
    verifyNoInteractions(storageVerifier);
  }

  private ResultActions create(String images) throws Exception {
    return mvc.perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(images)))
        .andExpect(status().isCreated());
  }

  private ResultActions update(Long id, String images) throws Exception {
    return mvc.perform(
        patch("/api/v1/artworks/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body(images)));
  }

  private Long artworkId() {
    return entityManager
        .createQuery(
            "select a.id from DisplayArtwork a where a.display.id = :displayId", Long.class)
        .setParameter("displayId", displayId)
        .getSingleResult();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private String body(String images) {
    return """
    {"displayId":%d,"artworkName":"영상 작품","content":"content","type":"MEDIA","productionYear":2026,
    "materialMedia":"영상","size":"800x600","point":"point","images":[%s],
    "artistName":"미디어 작가","artistUserId":%d,"coAuthors":{"userIds":[],"rawNames":[]},"qaHandlerUserIds":[%d]}
    """
        .formatted(displayId, images, ownerId, ownerId);
  }

  private static String videoJson() {
    return """
    {"imageUrl":"%s","mediaType":"VIDEO","thumbnailUrl":"%s","imageType":"ARTWORK","isThumbnail":true,"sortOrder":0,"width":800,"height":600}
    """
        .formatted(VIDEO, THUMBNAIL);
  }

  private static String legacyImageJson() {
    return """
    {"imageUrl":"%s","imageType":"ARTWORK","isThumbnail":true,"sortOrder":0,"width":800,"height":600}
    """
        .formatted(IMAGE);
  }

  private static Stream<String> invalidImages() {
    return Stream.of(
        videoJson() + "," + videoJson(),
        legacyImageJson() + "," + videoJson().replace("ARTWORK", "WORK_PROCESS"),
        videoJson().replace("\"thumbnailUrl\":\"" + THUMBNAIL + "\",", ""),
        String.join(",", IntStream.range(0, 11).mapToObj(i -> legacyImageJson()).toList()),
        legacyImageJson().replace("ARTWORK", "WORK_PROCESS"));
  }
}
