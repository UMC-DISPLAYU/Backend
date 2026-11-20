package com.example.demo.domain.displayartwork.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
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
@RecordApplicationEvents
@Transactional
class DisplayArtworkMediaControllerTest {
  private static final String VIDEO = "https://cdn.example/artwork/movie.mp4";
  private static final String THUMBNAIL = "https://cdn.example/artwork/thumbnail.png";
  private static final String IMAGE = "https://cdn.example/artwork/image.png";
  @Autowired private MockMvc mvc;
  @Autowired private ApplicationEvents events;
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

  @ParameterizedTest
  @MethodSource("emptyMaterials")
  void optionalMaterialRoundTripsAndClearsOnUpdate(String material) throws Exception {
    String request = body(legacyImageJson()).replace("\"materialMedia\":\"영상\",", material);
    mvc.perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.materialMedia").isEmpty());
    Long id = artworkId();
    update(id, legacyImageJson())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.materialMedia").value("영상"));
    mvc.perform(
            patch("/api/v1/artworks/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.materialMedia").isEmpty());
    entityManager.flush();
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, id).getMaterialMedia()).isNull();
    for (String suffix : List.of("", "/edit")) {
      mvc.perform(
              get("/api/v1/artworks/" + id + suffix)
                  .header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.materialMedia").isEmpty());
    }
  }

  private static Stream<String> emptyMaterials() {
    return Stream.of("", "\"materialMedia\":null,", "\"materialMedia\":\"   \",");
  }

  private static final String CROP = "{\"x\":0.1,\"y\":0.2,\"w\":0.5,\"h\":0.6}";

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void cropRoundTripsThroughWriteListPreviewDetailAndEdit(boolean video) throws Exception {
    String media = video ? videoJson() : legacyImageJson();
    createWithCrop(media, CROP)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, id).getArtworkCrop().getX())
        .isEqualByComparingTo("0.1");
    for (String suffix : List.of("", "/edit")) {
      mvc.perform(
              get("/api/v1/artworks/" + id + suffix)
                  .header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.artworkCrop.w").value(0.5));
    }
    mvc.perform(get("/api/v1/artworks").param("displayId", displayId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkCrop.y").value(0.2));
    mvc.perform(
            get("/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkCrop.h").value(0.6));
    update(id, media)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    updateWithCrop(id, media, "null")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    updateWithCrop(id, media, "{\"x\":0,\"y\":0,\"w\":1,\"h\":1}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.w").value(1));
  }

  @Test
  void videoCropTracksDisplayedThumbnailRatherThanVideoOriginal() throws Exception {
    createWithCrop(videoJson(), CROP).andExpect(status().isCreated());
    Long id = artworkId();
    String newVideo = videoJson().replace(VIDEO, "https://cdn.example/new.mp4");
    update(id, newVideo)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    update(id, newVideo.replace(THUMBNAIL, "https://cdn.example/new-thumbnail.png"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop").isEmpty());
    updateWithCrop(id, videoJson(), CROP)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    update(id, legacyImageJson().replace(IMAGE, THUMBNAIL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    update(id, legacyImageJson())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop").isEmpty());
  }

  @Test
  void selectingAnotherRepresentativeResetsCropAndExplicitCropWins() throws Exception {
    createWithCrop(legacyImageJson(), CROP).andExpect(status().isCreated());
    Long id = artworkId();
    String images =
        legacyImageJson().replace("\"isThumbnail\":true", "\"isThumbnail\":false")
            + ","
            + legacyImageJson().replace(IMAGE, "https://cdn.example/second.png");
    update(id, images)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop").isEmpty());
    updateWithCrop(id, legacyImageJson(), CROP)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.w").value(0.5));
  }

  @Test
  void cropTracksPersistedRepresentativeWhenRequestImagesAreNotInSortOrder() throws Exception {
    String first = legacyImageJson().replace("\"isThumbnail\":true", "\"isThumbnail\":false");
    String second =
        first
            .replace(IMAGE, "https://cdn.example/second.png")
            .replace("\"sortOrder\":0", "\"sortOrder\":1");
    createWithCrop(first + "," + second, CROP).andExpect(status().isCreated());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    update(id, second + "," + first)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkCrop.x").value(0.1));
    entityManager.flush();
    entityManager.clear();
    mvc.perform(get("/api/v1/artworks").param("displayId", displayId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkImageUrl").value(IMAGE))
        .andExpect(jsonPath("$.success.data.artworks[0].artworkCrop.x").value(0.1));
  }

  @Test
  void cropPreservesTenDecimalPlacesAtImageBoundary() throws Exception {
    String precise = "{\"x\":0.9999999999,\"y\":0,\"w\":0.0000000001,\"h\":1}";
    createWithCrop(legacyImageJson(), precise).andExpect(status().isCreated());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    var crop = entityManager.find(DisplayArtwork.class, id).getArtworkCrop();
    assertThat(crop.getX()).isEqualByComparingTo("0.9999999999");
    assertThat(crop.getW()).isEqualByComparingTo("0.0000000001");
  }

  @ParameterizedTest
  @MethodSource("invalidCrops")
  void invalidCropRejectsCreateAndUpdateWithoutChangingStoredMedia(String crop) throws Exception {
    createWithCrop(legacyImageJson(), crop).andExpect(status().isBadRequest());
    createWithCrop(legacyImageJson(), CROP).andExpect(status().isCreated());
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();
    updateWithCrop(id, videoJson(), crop).andExpect(status().isBadRequest());
    entityManager.clear();
    DisplayArtwork artwork = entityManager.find(DisplayArtwork.class, id);
    assertThat(artwork.getImages().getFirst().getImageUrl()).isEqualTo(IMAGE);
    assertThat(artwork.getArtworkCrop().getX()).isEqualByComparingTo("0.1");
  }

  @Test
  void cropUpdateRequiresArtworkEditorPermission() throws Exception {
    createWithCrop(legacyImageJson(), CROP).andExpect(status().isCreated());
    Long id = artworkId();
    mvc.perform(
            patch("/api/v1/artworks/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId + 1000))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyWithCrop(legacyImageJson(), "{\"x\":0,\"y\":0,\"w\":1,\"h\":1}")))
        .andExpect(status().isForbidden());
    entityManager.flush();
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, id).getArtworkCrop().getX())
        .isEqualByComparingTo("0.1");
  }

  @Test
  void artworkFitDefaultsToFillAndIsKeptWhenOmittedOnUpdate() throws Exception {
    create(legacyImageJson()).andExpect(jsonPath("$.success.data.artworkFit").value("FILL"));
    Long id = artworkId();

    updateWithFit(id, "\"FIT\"")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkFit").value("FIT"));
    update(id, legacyImageJson())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkFit").value("FIT"));
    updateWithFit(id, "null")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkFit").value("FIT"));
    updateWithFit(id, "\"FILL\"")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkFit").value("FILL"));
    entityManager.flush();
    entityManager.clear();
    assertThat(entityManager.find(DisplayArtwork.class, id).getArtworkFit())
        .isEqualTo(ArtworkFit.FILL);
  }

  @Test
  void artworkFitIsReturnedByDetailEditAndAllCardQueries() throws Exception {
    mvc.perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyWithFit(legacyImageJson(), "\"FIT\"")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.artworkFit").value("FIT"));
    Long id = artworkId();
    entityManager.flush();
    entityManager.clear();

    for (String suffix : List.of("", "/edit")) {
      mvc.perform(
              get("/api/v1/artworks/" + id + suffix)
                  .header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.artworkFit").value("FIT"));
    }
    mvc.perform(get("/api/v1/artworks").param("displayId", displayId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkFit").value("FIT"));
    mvc.perform(get("/api/v1/artworks").param("userId", ownerId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkFit").value("FIT"));
    mvc.perform(
            get("/api/v1/artworks/preview")
                .param("type", "RECOMMEND")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkFit").value("FIT"));
    mvc.perform(get("/api/v1/artworks/search"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworks[0].artworkFit").value("FIT"));
  }

  @Test
  void unknownArtworkFitIsRejected() throws Exception {
    mvc.perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyWithFit(legacyImageJson(), "\"STRETCH\"")))
        .andExpect(status().isBadRequest());
  }

  private ResultActions updateWithFit(Long id, String fit) throws Exception {
    return mvc.perform(
        patch("/api/v1/artworks/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyWithFit(legacyImageJson(), fit)));
  }

  private String bodyWithFit(String images, String fit) {
    return body(images).replace("\"artworkName\":", "\"artworkFit\":" + fit + ",\"artworkName\":");
  }

  private ResultActions createWithCrop(String images, String crop) throws Exception {
    return mvc.perform(
        post("/api/v1/artworks")
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyWithCrop(images, crop)));
  }

  private ResultActions updateWithCrop(Long id, String images, String crop) throws Exception {
    return mvc.perform(
        patch("/api/v1/artworks/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(ownerId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyWithCrop(images, crop)));
  }

  private String bodyWithCrop(String images, String crop) {
    return body(images)
        .replace("\"artworkName\":", "\"artworkCrop\":" + crop + ",\"artworkName\":");
  }

  private static Stream<String> invalidCrops() {
    return Stream.of(
        "{\"x\":-0.1,\"y\":0,\"w\":0.5,\"h\":1}",
        "{\"x\":0,\"y\":0,\"w\":0,\"h\":1}",
        "{\"x\":0.6,\"y\":0,\"w\":0.5,\"h\":1}",
        "{\"x\":0,\"y\":0.6,\"w\":1,\"h\":0.5}",
        "{\"x\":0.12345678901,\"y\":0,\"w\":0.5,\"h\":1}",
        "{\"x\":0,\"y\":0,\"w\":0.5}");
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

  @Test
  void videoReplacementCleanupPreservesReusedThumbnail() throws Exception {
    create(videoJson());
    Long id = artworkId();
    update(id, videoJson().replace(VIDEO, "https://cdn.example/artwork/new.mp4"))
        .andExpect(status().isOk());
    assertThat(events.stream(MediaFilesRemovedEvent.class).toList())
        .singleElement()
        .satisfies(event -> assertThat(event.fileUrls()).containsExactly(VIDEO));
  }

  @Test
  void deletingVideoArtworkSchedulesOriginalAndThumbnail() throws Exception {
    create(videoJson());
    Long id = artworkId();
    mvc.perform(
            delete("/api/v1/artworks/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(ownerId)))
        .andExpect(status().isOk());
    assertThat(events.stream(MediaFilesRemovedEvent.class).toList())
        .singleElement()
        .satisfies(
            event -> assertThat(event.fileUrls()).containsExactlyInAnyOrder(VIDEO, THUMBNAIL));
  }

  @Test
  void rejectedVideoReplacementCannotScheduleCleanup() throws Exception {
    create(videoJson());
    update(artworkId(), videoJson() + "," + videoJson()).andExpect(status().isBadRequest());
    assertThat(events.stream(MediaFilesRemovedEvent.class)).isEmpty();
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
