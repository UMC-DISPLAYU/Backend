package com.example.demo.domain.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.SpringDataContentReportJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:comment_report_test;MODE=MySQL;DATABASE_TO_UPPER=FALSE;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
      "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true",
      "spring.jpa.properties.hibernate.hbm2ddl.import_files=media-test-identities.sql"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentReportIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired EntityManager em;
  @Autowired TransactionTemplate tx;
  @Autowired TokenProvider tokens;
  @Autowired SpringDataContentReportJpaRepository reports;

  User author;
  User reporter;
  User otherReporter;
  DisplayReviewReply displayReply;
  ArtworkFeelingReply artworkReply;
  PersonalArtworkFeelingReply personalReply;
  String token;

  @BeforeEach
  void fixtures() {
    tx.executeWithoutResult(
        status -> {
          author = user();
          reporter = user();
          otherReporter = user();
          displayReply = DisplayReviewReply.create(101L, author.getId(), "답글", List.of());
          artworkReply = ArtworkFeelingReply.create(102L, author.getId(), "답글", List.of());
          personalReply = PersonalArtworkFeelingReply.create(103L, author.getId(), "답글", List.of());
          em.persist(displayReply);
          em.persist(artworkReply);
          em.persist(personalReply);
          em.flush();
        });
    token = tokens.createAccessToken(reporter);
  }

  @AfterEach
  void cleanup() {
    tx.executeWithoutResult(
        status -> {
          reports.deleteAllInBatch();
          remove(PersonalArtworkFeelingReply.class, personalReply.getPersonalFeelingReplyId());
          remove(ArtworkFeelingReply.class, artworkReply.getFeelingReplyId());
          remove(DisplayReviewReply.class, displayReply.getDisplayReviewReplyId());
          remove(User.class, otherReporter.getId());
          remove(User.class, reporter.getId());
          remove(User.class, author.getId());
        });
  }

  @ParameterizedTest
  @MethodSource("commentTargets")
  void reportsEverySupportedCommentType(ReportTargetType type) throws Exception {
    send(type, id(type), token, "{\"reasonCode\":\"OTHER\",\"description\":\"  상세 사유  \"}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.reportId").isNumber())
        .andExpect(jsonPath("$.success.data.reportedAt").isString());

    var saved = reports.findAll().getFirst();
    assertThat(saved.getTargetType()).isEqualTo(type);
    assertThat(saved.getTargetId()).isEqualTo(id(type));
    assertThat(saved.getReporterUserId()).isEqualTo(reporter.getId());
    assertThat(saved.getReportedUserId()).isEqualTo(author.getId());
    assertThat(saved.getReason()).isEqualTo(ReportReason.OTHER);
    assertThat(saved.getDescription()).isEqualTo("상세 사유");
  }

  @Test
  void rejectsDuplicateAndSelfReport() throws Exception {
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY,
            id(ReportTargetType.DISPLAY_REVIEW_REPLY),
            token,
            body())
        .andExpect(status().isOk());
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY,
            id(ReportTargetType.DISPLAY_REVIEW_REPLY),
            token,
            body())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONTENT_REPORT_ALREADY_EXISTS"));
    send(
            ReportTargetType.ARTWORK_FEELING_REPLY,
            id(ReportTargetType.ARTWORK_FEELING_REPLY),
            tokens.createAccessToken(author),
            body())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("SELF_REPORT_NOT_ALLOWED"));
  }

  @Test
  void rejectsAnonymousInvalidAndDeletedTargets() throws Exception {
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY,
            id(ReportTargetType.DISPLAY_REVIEW_REPLY),
            null,
            body())
        .andExpect(status().isUnauthorized());
    send("UNKNOWN", 1L, token, body()).andExpect(status().isBadRequest());
    send(ReportTargetType.DISPLAY_REVIEW_REPLY.name(), 0L, token, body())
        .andExpect(status().isBadRequest());
    send(ReportTargetType.DISPLAY_REVIEW_REPLY.name(), Long.MAX_VALUE, token, body())
        .andExpect(status().isNotFound());
    tx.executeWithoutResult(
        status ->
            em.find(DisplayReviewReply.class, displayReply.getDisplayReviewReplyId()).delete());
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY,
            id(ReportTargetType.DISPLAY_REVIEW_REPLY),
            token,
            body())
        .andExpect(status().isNotFound());
  }

  @Test
  void validatesReasonAndDescription() throws Exception {
    Long id = id(ReportTargetType.DISPLAY_REVIEW_REPLY);
    send(ReportTargetType.DISPLAY_REVIEW_REPLY.name(), id, token, "{}")
        .andExpect(status().isBadRequest());
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY.name(),
            id,
            token,
            "{\"reasonCode\":\"OTHER\",\"description\":\"" + "a".repeat(1001) + "\"}")
        .andExpect(status().isBadRequest());
    send(
            ReportTargetType.DISPLAY_REVIEW_REPLY.name(),
            id,
            token,
            "{\"reasonCode\":\"OTHER\",\"description\":\"   \"}")
        .andExpect(status().isOk());
    assertThat(reports.findAll().getFirst().getDescription()).isNull();
  }

  private static Stream<Arguments> commentTargets() {
    return Stream.of(
        Arguments.of(ReportTargetType.DISPLAY_REVIEW_REPLY),
        Arguments.of(ReportTargetType.ARTWORK_FEELING_REPLY),
        Arguments.of(ReportTargetType.PERSONAL_ARTWORK_FEELING_REPLY));
  }

  private ResultActions send(ReportTargetType type, Long id, String accessToken, String body)
      throws Exception {
    return send(type.name(), id, accessToken, body);
  }

  private ResultActions send(String type, Long id, String accessToken, String body)
      throws Exception {
    var request =
        post("/api/v1/comments/{commentType}/{commentId}/reports", type, id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body);
    if (accessToken != null) request.header("Authorization", "Bearer " + accessToken);
    return mvc.perform(request);
  }

  private Long id(ReportTargetType type) {
    return switch (type) {
      case DISPLAY_REVIEW_REPLY -> displayReply.getDisplayReviewReplyId();
      case ARTWORK_FEELING_REPLY -> artworkReply.getFeelingReplyId();
      case PERSONAL_ARTWORK_FEELING_REPLY -> personalReply.getPersonalFeelingReplyId();
      default -> throw new IllegalArgumentException();
    };
  }

  private String body() {
    return "{\"reasonCode\":\"OTHER\"}";
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

  private <T> void remove(Class<T> type, Long id) {
    T entity = em.find(type, id);
    if (entity != null) em.remove(entity);
  }
}
