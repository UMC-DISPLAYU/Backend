package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.infrastructure.persistence.AdminOperationJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.SpringDataAdminOperationJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

/** 트랜잭션 경계를 실제로 확인하려고 테스트 전체 트랜잭션을 쓰지 않고, 만든 자료는 끝나고 지운다. */
@SpringBootTest(
    properties = {
      "admin.comments.enabled=true",
      "admin.parents.enabled=true",
      "admin.operations.enabled=true"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminOperationLogIntegrationTest {
  private static final String BODY = "{\"reason\":\"  운영 검토  \"}";
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private SpringDataDisplayReviewJpaRepository reviews;
  @Autowired private SpringDataDisplayReviewReplyJpaRepository replies;
  @Autowired private SpringDataAdminOperationJpaRepository operations;
  @MockitoSpyBean private OperationLogPort logs;

  @AfterEach
  void cleanUp() {
    operations.deleteAll();
    replies.deleteAll();
    reviews.deleteAll();
    users.deleteAll();
  }

  @Test
  void deletingCommentStoresOperationAndAdministratorCanSearchIt() throws Exception {
    User admin = user(UserRole.ADMIN);
    DisplayReview review = reviews.saveAndFlush(DisplayReview.create(1L, 2L, "후기", List.of()));
    DisplayReviewReply reply =
        replies.saveAndFlush(
            DisplayReviewReply.create(review.getDisplayReviewId(), 2L, "답글", List.of()));
    String token = bearer(admin);

    mvc.perform(
            post(
                    "/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/{id}/delete",
                    reply.getDisplayReviewReplyId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk());

    DisplayReviewReply deleted = replies.findById(reply.getDisplayReviewReplyId()).orElseThrow();
    assertThat(deleted.isDeleted()).isTrue();
    List<AdminOperationJpaEntity> stored = operations.findAll();
    assertThat(stored).hasSize(1);
    AdminOperationJpaEntity operation = stored.getFirst();
    assertThat(operation.getTargetType()).isEqualTo("DISPLAY_REVIEW_REPLY");
    assertThat(operation.getTargetId()).isEqualTo(reply.getDisplayReviewReplyId());
    assertThat(operation.getAction()).isEqualTo("DELETE");
    assertThat(operation.getOperatorId()).isEqualTo(admin.getId());
    assertThat(operation.getReason()).isEqualTo("운영 검토");
    assertThat(operation.getProcessedAt()).isEqualTo(deleted.getDeletedAt());

    mvc.perform(
            get("/api/v1/admin/operations")
                .header("Authorization", token)
                .param("targetType", "DISPLAY_REVIEW_REPLY")
                .param("targetId", reply.getDisplayReviewReplyId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items.length()").value(1))
        .andExpect(
            jsonPath("$.success.data.items[0].operationId").value(operation.getOperationId()))
        .andExpect(jsonPath("$.success.data.items[0].operatorId").value(admin.getId()))
        .andExpect(jsonPath("$.success.data.items[0].reason").value("운영 검토"))
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
  }

  @Test
  void deletingReviewStoresOneOperationForParent() throws Exception {
    User admin = user(UserRole.ADMIN);
    DisplayReview review = reviews.saveAndFlush(DisplayReview.create(1L, 2L, "후기", List.of()));
    replies.saveAndFlush(
        DisplayReviewReply.create(review.getDisplayReviewId(), 2L, "답글", List.of()));

    mvc.perform(
            post("/api/v1/admin/parents/DISPLAY_REVIEW/{id}/delete", review.getDisplayReviewId())
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isOk());

    assertThat(operations.findAll())
        .singleElement()
        .satisfies(
            operation -> {
              assertThat(operation.getTargetType()).isEqualTo("DISPLAY_REVIEW");
              assertThat(operation.getTargetId()).isEqualTo(review.getDisplayReviewId());
            });
  }

  @Test
  void failedOperationRecordRollsBackCommentDeletion() throws Exception {
    User admin = user(UserRole.ADMIN);
    DisplayReview review = reviews.saveAndFlush(DisplayReview.create(1L, 2L, "후기", List.of()));
    DisplayReviewReply reply =
        replies.saveAndFlush(
            DisplayReviewReply.create(review.getDisplayReviewId(), 2L, "답글", List.of()));
    doThrow(new IllegalStateException("이력 저장 실패")).when(logs).record(any());

    mvc.perform(
            post(
                    "/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/{id}/delete",
                    reply.getDisplayReviewReplyId())
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isInternalServerError());

    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(operations.count()).isZero();
  }

  @Test
  void failedOperationRecordRollsBackReviewAndReplyDeletion() throws Exception {
    User admin = user(UserRole.ADMIN);
    DisplayReview review = reviews.saveAndFlush(DisplayReview.create(1L, 2L, "후기", List.of()));
    DisplayReviewReply reply =
        replies.saveAndFlush(
            DisplayReviewReply.create(review.getDisplayReviewId(), 2L, "답글", List.of()));
    doThrow(new IllegalStateException("이력 저장 실패")).when(logs).record(any());

    mvc.perform(
            post("/api/v1/admin/parents/DISPLAY_REVIEW/{id}/delete", review.getDisplayReviewId())
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isInternalServerError());

    assertThat(reviews.findById(review.getDisplayReviewId()).orElseThrow().isDeleted()).isFalse();
    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(operations.count()).isZero();
  }

  @Test
  void failedDeletionStoresNoOperation() throws Exception {
    User admin = user(UserRole.ADMIN);

    mvc.perform(
            post("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/{id}/delete", 999_999L)
                .header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
        .andExpect(status().isNotFound());

    assertThat(operations.count()).isZero();
  }

  @Test
  void ordinaryUserCannotSearchOperations() throws Exception {
    mvc.perform(
            get("/api/v1/admin/operations").header("Authorization", bearer(user(UserRole.USER))))
        .andExpect(status().isForbidden());
    mvc.perform(get("/api/v1/admin/operations")).andExpect(status().isUnauthorized());
  }

  private String bearer(User user) {
    return "Bearer " + tokens.createAccessToken(user);
  }

  private User user(UserRole role) {
    String id = UUID.randomUUID().toString();
    return users.saveAndFlush(
        User.builder()
            .provider(Provider.Kakao)
            .providerId(id)
            .name("사용자")
            .nickname(id)
            .socialEmail(id + "@example.com")
            .role(role)
            .build());
  }
}
