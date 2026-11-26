package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import com.example.demo.global.file.domain.MediaContent;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
class DisplayMediaControllerTest {
  private static final String VIDEO = "https://cdn.displayu.com/display/poster.mp4";
  private static final String THUMBNAIL = "https://cdn.displayu.com/display/thumbnail.jpg";
  private static final String IMAGE = "https://cdn.displayu.com/display/poster.jpg";
  private static final String VIDEO_MEDIA =
      "{\"imageUrl\":\""
          + VIDEO
          + "\",\"mediaType\":\"VIDEO\",\"thumbnailUrl\":\""
          + THUMBNAIL
          + "\"}";
  private static final String IMAGE_MEDIA =
      "{\"imageUrl\":\"" + IMAGE + "\",\"mediaType\":\"IMAGE\"}";

  @Autowired private MockMvc mvc;
  @Autowired private ApplicationEvents events;
  @Autowired private JwtFactory jwtFactory;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private MediaStorageVerifier storageVerifier;
  private String authorization;

  @BeforeEach
  void authenticate() {
    User user =
        users.save(
            User.builder()
                .provider(Provider.Google)
                .providerId("display-media")
                .name("영상 작가")
                .nickname("영상 작가")
                .socialEmail("media@displayu.com")
                .isVerified(true)
                .build());
    authorization = "Bearer " + jwtFactory.create(user.getId().toString(), 3600000L, "ACCESS");
  }

  @Test
  void videoRegistrationPersistsAndBothDetailPathsExposeOriginalAndThumbnail() throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA + ",\"displayMedia\":[" + IMAGE_MEDIA + "]")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(VIDEO))
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("VIDEO"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").value(THUMBNAIL))
        .andExpect(jsonPath("$.success.data.images[1].mediaType").value("IMAGE"));
    verify(storageVerifier).verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY);
    Display display = reload();
    assertThat(display.getPosterImageUrl()).isEqualTo(VIDEO);
    assertThat(display.getPosterCardImageUrl()).isEqualTo(THUMBNAIL);
    assertThat(display.getPosterVideoUrl()).isEqualTo(VIDEO);
    mvc.perform(get("/api/v1/display/{id}", display.getId()).header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.images[0].imageUrl").value(VIDEO))
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("VIDEO"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").value(THUMBNAIL));
  }

  @Test
  void acceptsNineAdditionalPostersAndRejectsATenth() throws Exception {
    String urls =
        IntStream.range(0, 9)
            .mapToObj(i -> "\"detail-" + i + ".jpg\"")
            .collect(Collectors.joining(","));
    String media =
        IntStream.range(0, 9)
            .mapToObj(i -> IMAGE_MEDIA.replace(IMAGE, "other-" + i + ".jpg"))
            .collect(Collectors.joining(","));
    create("\"posterImageUrl\":\"poster.jpg\",\"displayMedia\":[" + media + "," + IMAGE_MEDIA + "]")
        .andExpect(status().isBadRequest());
    create("\"posterImageUrl\":\"poster.jpg\",\"displayImageUrl\":[" + urls + "]")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.images.length()").value(10));
    Long id = reload().getId();
    update(id, "\"displayMedia\":[" + media + "]").andExpect(status().isOk());
    update(id, "\"displayMedia\":[" + media + "," + IMAGE_MEDIA + "]")
        .andExpect(status().isBadRequest());
    assertThat(reload().getImages()).hasSize(10);
  }

  @Test
  void legacyImageWritesRemainCompatible() throws Exception {
    create("\"posterImageUrl\":\"" + IMAGE + "\",\"displayImageUrl\":[\"detail.jpg\"]")
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.posterFit").value("FILL"))
        .andExpect(jsonPath("$.success.data.posterEdgeColor").value(nullValue()))
        .andExpect(jsonPath("$.success.data.images[0].mediaType").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.images[0].thumbnailUrl").isEmpty());
    Display display = reload();
    update(display.getId(), "\"posterImageUrl\":\"new.jpg\",\"displayImageUrl\":[]")
        .andExpect(status().isOk());
    assertThat(reload().getImages()).hasSize(1);
    assertThat(reload().getPosterImageUrl()).isEqualTo("new.jpg");
  }

  @Test
  void posterPresentationPersistsAndEachFieldCanBeUpdatedIndependently() throws Exception {
    create(
            """
            "posterImageUrl":"poster.jpg","posterFit":"FIT","posterEdgeColor":"#12AbEF"
            """)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.posterFit").value("FIT"))
        .andExpect(jsonPath("$.success.data.posterEdgeColor").value("#12AbEF"));
    Long id = reload().getId();

    update(id, "\"posterFit\":null,\"posterEdgeColor\":null")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posterFit").value("FIT"))
        .andExpect(jsonPath("$.success.data.posterEdgeColor").value("#12AbEF"));
    update(id, "\"posterEdgeColor\":\"#000000\"")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posterFit").value("FIT"));
    update(id, "\"posterFit\":\"FILL\"")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posterEdgeColor").value("#000000"));
    entityManager.flush();
    entityManager.clear();
    mvc.perform(get("/api/v1/display/{id}", id).header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posterFit").value("FILL"))
        .andExpect(jsonPath("$.success.data.posterEdgeColor").value("#000000"));
  }

  @Test
  void movingVideoToDetailsUsesFinalStateAndPreservesOccupiedRowIds() throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA + ",\"displayMedia\":[" + IMAGE_MEDIA + "]")
        .andExpect(status().isCreated());
    Display before = reload();
    Long mainId = image(before, DisplayImageType.MAIN).getId();
    Long detailId = image(before, DisplayImageType.DETAIL).getId();
    update(
            before.getId(),
            "\"posterMedia\":" + IMAGE_MEDIA + ",\"displayMedia\":[" + VIDEO_MEDIA + "]")
        .andExpect(status().isOk());
    Display after = reload();
    assertThat(image(after, DisplayImageType.MAIN).getId()).isEqualTo(mainId);
    assertThat(image(after, DisplayImageType.DETAIL).getId()).isEqualTo(detailId);
    assertThat(image(after, DisplayImageType.MAIN).getThumbnailUrl()).isNull();
    assertThat(image(after, DisplayImageType.DETAIL).getVideoUrl()).isEqualTo(VIDEO);
    assertThat(after.getPosterVideoUrl()).isNull();
  }

  @Test
  void posterTransitionsClearCropAndVideoMetadata() throws Exception {
    create(
            "\"posterMedia\":"
                + IMAGE_MEDIA
                + ",\"posterCrop\":{\"x\":0,\"y\":0,\"w\":0.5,\"h\":0.5}")
        .andExpect(status().isCreated());
    Display display = reload();
    assertThat(display.getPosterCrop()).isNotNull();
    update(display.getId(), "\"posterMedia\":" + VIDEO_MEDIA).andExpect(status().isOk());
    assertThat(reload().getPosterCrop()).isNull();
    update(display.getId(), "\"posterMedia\":" + IMAGE_MEDIA).andExpect(status().isOk());
    Display after = reload();
    assertThat(after.getPosterVideoUrl()).isNull();
    assertThat(image(after, DisplayImageType.MAIN).getThumbnailUrl()).isNull();
  }

  @Test
  void textOnlyAndNullMediaUpdatesPreserveVideoWithoutStorageAccess() throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA).andExpect(status().isCreated());
    Display display = reload();
    clearInvocations(storageVerifier);
    update(display.getId(), "\"title\":\"제목 수정\",\"posterMedia\":null,\"displayMedia\":null")
        .andExpect(status().isOk());
    verifyNoInteractions(storageVerifier);
    entityManager.flush();
    entityManager.clear();
    Display after = entityManager.find(Display.class, display.getId());
    assertThat(after.getTitle()).isEqualTo("제목 수정");
    assertThat(after.getPosterVideoUrl()).isEqualTo(VIDEO);
  }

  @Test
  void failedStorageVerificationCannotMutateExistingTitleOrMedia() throws Exception {
    create("\"posterMedia\":" + IMAGE_MEDIA).andExpect(status().isCreated());
    Display display = reload();
    doThrow(new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "업로드 검증 실패"))
        .when(storageVerifier)
        .verifyVideo(any(), any());
    update(display.getId(), "\"title\":\"실패한 수정\",\"posterMedia\":" + VIDEO_MEDIA)
        .andExpect(status().isBadRequest());
    assertThat(display.getTitle()).isEqualTo("영상 전시");
    assertThat(display.getPosterImageUrl()).isEqualTo(IMAGE);
    assertThat(reload().getPosterImageUrl()).isEqualTo(IMAGE);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "\"displayMedia\":[{\"imageUrl\":\"other.mp4\",\"mediaType\":\"VIDEO\",\"thumbnailUrl\":\"other.jpg\"}]",
        "\"posterCrop\":{\"x\":0,\"y\":0,\"w\":1,\"h\":1}",
        "\"posterFit\":\"STRETCH\"",
        "\"posterEdgeColor\":\"#123\"",
        "\"posterImageUrl\":\"https://cdn.displayu.com/display/poster.mp4\""
      })
  void invalidPartialUpdatesLeaveExistingMediaUntouched(String fields) throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA).andExpect(status().isCreated());
    Display display = reload();
    update(display.getId(), fields).andExpect(status().isBadRequest());
    assertThat(display.getPosterVideoUrl()).isEqualTo(VIDEO);
    assertThat(display.getImages()).hasSize(1);
    assertThat(reload().getPosterVideoUrl()).isEqualTo(VIDEO);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "\"posterImageUrl\":\" \"",
        "\"posterImageUrl\":\"poster.jpg\",\"posterFit\":\"STRETCH\"",
        "\"posterImageUrl\":\"poster.jpg\",\"posterEdgeColor\":\"ff0000\"",
        "\"posterMedia\":{\"imageUrl\":\"poster.jpg\"}",
        "\"posterMedia\":{\"imageUrl\":null,\"mediaType\":\"IMAGE\"}",
        "\"posterMedia\":{\"imageUrl\":\"poster.mp4\",\"mediaType\":\"VIDEO\"}",
        "\"posterMedia\":{\"imageUrl\":\"poster.jpg\",\"mediaType\":\"IMAGE\",\"thumbnailUrl\":\"thumb.jpg\"}",
        "\"posterImageUrl\":\"legacy.jpg\",\"posterMedia\":{\"imageUrl\":\"poster.jpg\",\"mediaType\":\"IMAGE\"}",
        "\"posterImageUrl\":\"legacy.jpg\",\"displayImageUrl\":[],\"displayMedia\":[]",
        "\"posterImageUrl\":\"legacy.jpg\",\"displayMedia\":[null]",
        "\"posterImageUrl\":\"legacy.jpg\",\"displayMedia\":[{\"imageUrl\":\" \",\"mediaType\":\"IMAGE\"}]"
      })
  void rejectsInvalidCreatePayloadsWithoutPersistingDisplay(String fields) throws Exception {
    create(fields).andExpect(status().isBadRequest());
    assertThat(entityManager.createQuery("select d from Display d", Display.class).getResultList())
        .isEmpty();
  }

  @Test
  void replacementCleanupKeepsThumbnailReusedAsNewPoster() throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA).andExpect(status().isCreated());
    Display display = reload();
    update(display.getId(), "\"posterImageUrl\":\"" + THUMBNAIL + "\"").andExpect(status().isOk());
    assertThat(events.stream(MediaFilesRemovedEvent.class).toList())
        .singleElement()
        .satisfies(event -> assertThat(event.fileUrls()).containsExactly(VIDEO));
  }

  @Test
  void rejectedReplacementPublishesNoCleanupEvent() throws Exception {
    create("\"posterMedia\":" + VIDEO_MEDIA).andExpect(status().isCreated());
    Display display = reload();
    update(display.getId(), "\"displayMedia\":[" + VIDEO_MEDIA + "]")
        .andExpect(status().isBadRequest());
    assertThat(events.stream(MediaFilesRemovedEvent.class)).isEmpty();
  }

  private ResultActions create(String mediaFields) throws Exception {
    String body =
        """
        {"title":"영상 전시","type":"GRADUATION","fields":["GRAPHIC"],"region":"SEOUL",
        "schoolOrOrganization":"학교","departmentOrClub":"학과","displayNickname":"팀장",
        "startDate":"2026-05-28","endDate":"2026-06-05","openTime":"10:00","closeTime":"18:00",
        "locationName":"전시장","latitude":37.5,"longitude":127,"roadAddress":"서울"%s}
        """
            .formatted(mediaFields.isEmpty() ? "" : "," + mediaFields);
    return mvc.perform(
        post("/api/v1/display")
            .header("Authorization", authorization)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));
  }

  private ResultActions update(Long id, String fields) throws Exception {
    return mvc.perform(
        patch("/api/v1/display")
            .header("Authorization", authorization)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"displayId\":" + id + "," + fields + "}"));
  }

  private Display reload() {
    entityManager.flush();
    entityManager.clear();
    return entityManager
        .createQuery("select d from Display d where d.title = :title", Display.class)
        .setParameter("title", "영상 전시")
        .getSingleResult();
  }

  private DisplayImage image(Display display, DisplayImageType type) {
    return display.getImages().stream()
        .filter(image -> image.getImageType() == type)
        .findFirst()
        .orElseThrow();
  }
}
