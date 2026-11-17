package com.example.demo.domain.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.*;
import com.example.demo.domain.display.domain.vo.*;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.report.domain.error.ReportErrorCode;
import com.example.demo.domain.report.domain.model.ContentReport;
import com.example.demo.domain.report.domain.repository.ContentReportRepository;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.domain.report.infrastructure.persistence.SpringDataContentReportJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:content_report_test;MODE=MySQL;DATABASE_TO_UPPER=FALSE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "springdoc.api-docs.enabled=true",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true",
      "spring.jpa.properties.hibernate.hbm2ddl.import_files=media-test-identities.sql"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContentReportIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired EntityManager em;
  @Autowired TransactionTemplate tx;
  @Autowired TokenProvider tokens;
  @Autowired SpringDataContentReportJpaRepository reports;
  @Autowired ContentReportRepository reportRepository;
  User author, reporter, otherReporter;
  Display display, otherDisplay;
  DisplayArtwork artwork, otherArtwork;
  DisplayReview review;
  ArtworkFeeling feeling;
  String token;

  @BeforeEach
  void fixtures() {
    tx.executeWithoutResult(
        status -> {
          author = user();
          reporter = user();
          otherReporter = user();
          display = display();
          otherDisplay = display();
          artwork = artwork(display);
          otherArtwork = artwork(otherDisplay);
          review = DisplayReview.create(display.getId(), author.getId(), "신고할 후기", List.of());
          feeling = ArtworkFeeling.create(artwork.getId(), author.getId(), "신고할 방명록", List.of());
          em.persist(review);
          em.persist(feeling);
          em.flush();
        });
    token = tokens.createAccessToken(reporter);
  }

  @AfterEach
  void cleanup() {
    tx.executeWithoutResult(
        status -> {
          reports.deleteAllInBatch();
          for (Object item :
              List.of(
                  review,
                  feeling,
                  artwork,
                  otherArtwork,
                  display,
                  otherDisplay,
                  author,
                  reporter,
                  otherReporter)) {
            em.remove(
                em.find(
                    item.getClass(),
                    em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(item)));
          }
        });
  }

  @ParameterizedTest
  @EnumSource(ReportReason.class)
  void bothEndpointsStoreAllReasonsAndServerDerivedIdentities(ReportReason reason)
      throws Exception {
    LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);
    for (boolean isReview : List.of(true, false)) {
      String body =
          "{\"reason\":\""
              + reason.name()
              + "\",\"reporterUserId\":"
              + author.getId()
              + ",\"reportedUserId\":"
              + otherReporter.getId()
              + ",\"targetType\":\"FAKE\",\"targetId\":999}";
      send(path(isReview), token, body)
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.reportId").isNumber())
          .andExpect(jsonPath("$.success.data.createdAt").isString());
    }
    assertThat(reports.findAll())
        .hasSize(2)
        .allSatisfy(
            row -> {
              assertThat(row.getReporterUserId()).isEqualTo(reporter.getId());
              assertThat(row.getReportedUserId()).isEqualTo(author.getId());
              assertThat(row.getReason()).isEqualTo(reason);
              assertThat(row.getCreatedAt())
                  .isBetween(before, LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1));
              assertThat(row.getTargetId())
                  .isEqualTo(
                      row.getTargetType() == ReportTargetType.DISPLAY_REVIEW
                          ? review.getDisplayReviewId()
                          : feeling.getFeelingId());
            });
    tx.executeWithoutResult(
        status -> {
          assertThat(em.find(DisplayReview.class, review.getDisplayReviewId()).getContent())
              .isEqualTo("신고할 후기");
          assertThat(em.find(ArtworkFeeling.class, feeling.getFeelingId()).getContent())
              .isEqualTo("신고할 방명록");
          assertThat(em.find(DisplayReview.class, review.getDisplayReviewId()).isDeleted())
              .isFalse();
          assertThat(em.find(ArtworkFeeling.class, feeling.getFeelingId()).isDeleted()).isFalse();
        });
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void duplicateDifferentReasonReturns409WhileAnotherReporterCanReport(boolean isReview)
      throws Exception {
    send(path(isReview), token, body("OTHER")).andExpect(status().isOk());
    send(path(isReview), token, body("ABUSE_DEFAMATION"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONTENT_REPORT_ALREADY_EXISTS"))
        .andExpect(jsonPath("$.error.message").value("이미 신고한 댓글입니다."));
    assertThat(reports.count()).isEqualTo(1);
    send(path(isReview), tokens.createAccessToken(otherReporter), body("OTHER"))
        .andExpect(status().isOk());
    assertThat(reports.findAll())
        .hasSize(2)
        .extracting(ContentReportJpaEntity::getReporterUserId)
        .containsExactlyInAnyOrder(reporter.getId(), otherReporter.getId());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rejectsAnonymousSelfReportsAndInvalidBodiesWithoutSaving(boolean isReview) throws Exception {
    send(path(isReview), null, body("OTHER")).andExpect(status().isUnauthorized());
    send(path(isReview), tokens.createAccessToken(author), body("OTHER"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("SELF_REPORT_NOT_ALLOWED"));
    for (String invalid : List.of("{}", "{\"reason\":null}", body("UNKNOWN"))) {
      send(path(isReview), token, invalid).andExpect(status().isBadRequest());
    }
    for (long id : List.of(0L, -1L)) {
      send(path(isReview, id, targetId(isReview)), token, body("OTHER"))
          .andExpect(status().isBadRequest());
      send(path(isReview, parentId(isReview), id), token, body("OTHER"))
          .andExpect(status().isBadRequest());
    }
    assertThat(reports.count()).isZero();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rejectsMissingDeletedAndWrongParentTargets(boolean isReview) throws Exception {
    send(path(isReview, Long.MAX_VALUE, targetId(isReview)), token, body("OTHER"))
        .andExpect(status().isNotFound());
    send(path(isReview, parentId(isReview), Long.MAX_VALUE), token, body("OTHER"))
        .andExpect(status().isNotFound());
    send(
            path(
                isReview,
                isReview ? otherDisplay.getId() : otherArtwork.getId(),
                targetId(isReview)),
            token,
            body("OTHER"))
        .andExpect(status().isNotFound());
    tx.executeWithoutResult(
        status -> {
          if (isReview) em.find(DisplayReview.class, review.getDisplayReviewId()).delete();
          else em.find(ArtworkFeeling.class, feeling.getFeelingId()).delete();
        });
    send(path(isReview), token, body("OTHER")).andExpect(status().isNotFound());
    assertThat(reports.count()).isZero();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void rejectsDeletedParent(boolean isReview) throws Exception {
    tx.executeWithoutResult(
        status -> {
          if (isReview) em.find(Display.class, display.getId()).delete();
          else em.find(DisplayArtwork.class, artwork.getId()).delete();
        });
    send(path(isReview), token, body("OTHER")).andExpect(status().isNotFound());
    assertThat(reports.count()).isZero();
  }

  @Test
  void withdrawnReporterCannotUsePreviouslyIssuedToken() throws Exception {
    tx.executeWithoutResult(
        status ->
            em.find(User.class, reporter.getId()).withdraw(LocalDateTime.now(ZoneOffset.UTC)));
    for (boolean isReview : List.of(true, false))
      send(path(isReview), token, body("OTHER")).andExpect(status().isUnauthorized());
    assertThat(reports.count()).isZero();
  }

  @Test
  void laterTargetDeletionKeepsReports() throws Exception {
    for (boolean isReview : List.of(true, false))
      send(path(isReview), token, body("OTHER")).andExpect(status().isOk());
    tx.executeWithoutResult(
        status -> {
          em.find(DisplayReview.class, review.getDisplayReviewId()).delete();
          em.find(ArtworkFeeling.class, feeling.getFeelingId()).delete();
        });
    assertThat(reports.count()).isEqualTo(2);
  }

  @Test
  void sameNumericTargetIdIsDistinctAcrossTargetTypes() {
    tx.executeWithoutResult(
        status -> {
          for (ReportTargetType type : ReportTargetType.values())
            reportRepository.save(
                ContentReport.create(
                    type,
                    700L,
                    reporter.getId(),
                    author.getId(),
                    ReportReason.OTHER,
                    LocalDateTime.now(ZoneOffset.UTC)));
        });
    assertThat(reports.findAll())
        .hasSize(2)
        .extracting(ContentReportJpaEntity::getTargetType)
        .containsExactlyInAnyOrder(
            ReportTargetType.DISPLAY_REVIEW, ReportTargetType.ARTWORK_FEELING);
  }

  @Test
  void concurrentTransactionsSaveExactlyOneReport() throws Exception {
    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
      List<Future<String>> attempts = new ArrayList<>();
      for (ReportReason reason : List.of(ReportReason.OTHER, ReportReason.ABUSE_DEFAMATION)) {
        attempts.add(
            executor.submit(
                () -> {
                  try {
                    return tx.execute(
                        status -> {
                          ready.countDown();
                          try {
                            if (!start.await(10, TimeUnit.SECONDS))
                              throw new AssertionError("신고 동시 시작 시간 초과");
                          } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new AssertionError(e);
                          }
                          reportRepository.save(
                              ContentReport.create(
                                  ReportTargetType.DISPLAY_REVIEW,
                                  review.getDisplayReviewId(),
                                  reporter.getId(),
                                  author.getId(),
                                  reason,
                                  LocalDateTime.now(ZoneOffset.UTC)));
                          return "saved";
                        });
                  } catch (BusinessException e) {
                    assertThat(e.errorCode())
                        .isEqualTo(ReportErrorCode.CONTENT_REPORT_ALREADY_EXISTS);
                    assertThat(e.getMessage()).isEqualTo("이미 신고한 댓글입니다.");
                    return "duplicate";
                  }
                }));
      }
      try {
        assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
      } finally {
        start.countDown();
      }
      assertThat(
              List.of(
                  attempts.get(0).get(15, TimeUnit.SECONDS),
                  attempts.get(1).get(15, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder("saved", "duplicate");
    }
    assertThat(reports.count()).isEqualTo(1);
  }

  @Test
  void openApiDocumentsExactlyTwoCreationRoutesAndRequiredReasons() throws Exception {
    JsonNode doc =
        JsonMapper.builder()
            .build()
            .readTree(
                mvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());
    List<String> paths = new ArrayList<>();
    doc.path("paths")
        .properties()
        .forEach(
            entry -> {
              if (entry.getKey().endsWith("/reports")) paths.add(entry.getKey());
            });
    assertThat(paths)
        .containsExactlyInAnyOrder(
            "/api/v1/display/{displayId}/reviews/{displayReviewId}/reports",
            "/api/v1/artworks/{artworkId}/feelings/{feelingId}/reports");
    for (String path : paths) {
      JsonNode op = doc.path("paths").path(path).path("post");
      JsonNode request =
          resolve(
              doc, op.path("requestBody").path("content").path("application/json").path("schema"));
      assertThat(request.path("required").toString()).contains("reason");
      assertThat(request.path("properties").size()).isEqualTo(1);
      JsonNode reason = resolve(doc, request.path("properties").path("reason"));
      assertThat(reason.path("enum").valueStream().map(JsonNode::asText).toList())
          .containsExactlyInAnyOrder(
              "SPAM_ADVERTISEMENT",
              "ABUSE_DEFAMATION",
              "OBSCENE_OFFENSIVE_CONTENT",
              "PERSONAL_INFORMATION_EXPOSURE",
              "OTHER");
      for (String code : List.of("200", "400", "401", "403", "404", "409"))
        assertThat(op.path("responses").has(code)).as(path + " " + code).isTrue();
      assertThat(op.path("responses").path("409").path("description").asText())
          .contains("이미 신고한 댓글입니다.");
      JsonNode response =
          op.path("responses").path("200").path("content").path("*/*").path("schema");
      JsonNode success = resolve(doc, resolve(doc, response).path("properties").path("success"));
      JsonNode data = resolve(doc, success.path("properties").path("data"));
      assertThat(data.path("properties").has("reportId")).isTrue();
      assertThat(data.path("properties").has("createdAt")).isTrue();
    }
  }

  private static JsonNode resolve(JsonNode doc, JsonNode node) {
    return node.has("$ref") ? doc.at(node.path("$ref").asText().substring(1)) : node;
  }

  private ResultActions send(String path, String accessToken, String body) throws Exception {
    var request = post(path).contentType(MediaType.APPLICATION_JSON).content(body);
    if (accessToken != null) request.header("Authorization", "Bearer " + accessToken);
    return mvc.perform(request);
  }

  private static String body(String reason) {
    return "{\"reason\":\"" + reason + "\"}";
  }

  private Long targetId(boolean isReview) {
    return isReview ? review.getDisplayReviewId() : feeling.getFeelingId();
  }

  private Long parentId(boolean isReview) {
    return isReview ? display.getId() : artwork.getId();
  }

  private String path(boolean isReview) {
    return path(isReview, parentId(isReview), targetId(isReview));
  }

  private static String path(boolean isReview, Long parent, Long target) {
    return isReview
        ? "/api/v1/display/" + parent + "/reviews/" + target + "/reports"
        : "/api/v1/artworks/" + parent + "/feelings/" + target + "/reports";
  }

  private User user() {
    String id = UUID.randomUUID().toString();
    User user =
        User.builder()
            .provider(Provider.Kakao)
            .providerId(id)
            .name("사용자")
            .nickname(id)
            .socialEmail(id + "@example.com")
            .role(UserRole.USER)
            .build();
    em.persist(user);
    return user;
  }

  private Display display() {
    Display value =
        Display.create(
            new UserId(author.getId()),
            "신고 테스트 전시",
            "https://example.com/poster.jpg",
            "부제",
            "설명",
            new DisplayLocation("전시장", BigDecimal.ONE, BigDecimal.ONE),
            "",
            "",
            "학교",
            "학과",
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
    value.publish();
    em.persist(value);
    return value;
  }

  private DisplayArtwork artwork(Display parent) {
    DisplayArtwork value =
        DisplayArtwork.create(
            parent,
            "작품",
            "설명",
            List.of(ArtworkType.PAINTING),
            2026,
            "재료",
            "크기",
            "감상 포인트",
            0,
            author.getId(),
            List.of(
                new ArtworkImage(
                    null,
                    "https://example.com/art.jpg",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    100,
                    100)));
    em.persist(value);
    return value;
  }
}
