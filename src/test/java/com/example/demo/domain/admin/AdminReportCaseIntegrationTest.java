package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.infrastructure.persistence.AdminReportCaseJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.SpringDataAdminReportCaseJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.domain.report.infrastructure.persistence.SpringDataContentReportJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** 삭제·종결의 트랜잭션 경계를 실제로 확인하려고 테스트 전체 트랜잭션을 쓰지 않고, 만든 자료는 끝나고 지운다. */
@SpringBootTest(
    properties = {
      "admin.report-cases.enabled=true",
      "admin.report-resolution.enabled=true",
      "admin.comments.enabled=true"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminReportCaseIntegrationTest {
  private static final String CASES = "/api/v1/admin/report-cases";
  private static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 20, 3, 0);
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private SpringDataDisplayReviewJpaRepository reviews;
  @Autowired private SpringDataDisplayReviewReplyJpaRepository replies;
  @Autowired private SpringDataContentReportJpaRepository reports;
  @MockitoSpyBean private SpringDataAdminReportCaseJpaRepository cases;
  private String token;
  private Long adminId;

  @BeforeEach
  void setUp() {
    User admin = user(UserRole.ADMIN);
    adminId = admin.getId();
    token = "Bearer " + tokens.createAccessToken(admin);
  }

  @AfterEach
  void cleanUp() {
    cases.deleteAll();
    reports.deleteAll();
    replies.deleteAll();
    reviews.deleteAll();
    users.deleteAll();
  }

  @Test
  void pendingListGroupsCommentReportsAndPagesByCaseId() throws Exception {
    DisplayReviewReply first = reply();
    DisplayReviewReply second = reply();
    ContentReportJpaEntity firstCase = report(first, 101L, 0);
    report(first, 102L, 5);
    ContentReportJpaEntity secondCase = report(second, 101L, 7);
    report(first, 103L, 9);
    reports.saveAndFlush(
        new ContentReportJpaEntity(
            null,
            ReportTargetType.DISPLAY_REVIEW,
            first.getDisplayReviewId(),
            101L,
            2L,
            ReportReason.OTHER,
            null,
            BASE));

    get(CASES + "?status=PENDING&size=1")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items.length()").value(1))
        .andExpect(jsonPath("$.success.data.items[0].reportCaseId").value(secondCase.getReportId()))
        .andExpect(jsonPath("$.success.data.items[0].status").value("PENDING"))
        .andExpect(jsonPath("$.success.data.items[0].commentType").value("DISPLAY_REVIEW_REPLY"))
        .andExpect(
            jsonPath("$.success.data.items[0].commentId").value(second.getDisplayReviewReplyId()))
        .andExpect(jsonPath("$.success.data.items[0].reportCount").value(1))
        .andExpect(jsonPath("$.success.data.nextCursor").value(secondCase.getReportId()))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));
    get(CASES + "?status=PENDING&size=1&cursor=" + secondCase.getReportId())
        .andExpect(jsonPath("$.success.data.items.length()").value(1))
        .andExpect(jsonPath("$.success.data.items[0].reportCaseId").value(firstCase.getReportId()))
        .andExpect(jsonPath("$.success.data.items[0].reportCount").value(3))
        .andExpect(
            jsonPath("$.success.data.items[0].lastReportedAt")
                .value(BASE.plusMinutes(9).toInstant(ZoneOffset.UTC).toString()))
        .andExpect(jsonPath("$.success.data.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
    get(CASES + "?status=RESOLVED")
        .andExpect(jsonPath("$.success.data.items").isEmpty())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
  }

  @Test
  void detailReturnsWholeRoundWithCommentAndParent() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);
    ContentReportJpaEntity second = report(reply, 102L, 5);

    get(CASES + "/" + first.getReportId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.status").value("PENDING"))
        .andExpect(jsonPath("$.success.data.reportCount").value(2))
        .andExpect(jsonPath("$.success.data.comment.content").value("답글"))
        .andExpect(jsonPath("$.success.data.parent.parentType").value("DISPLAY_REVIEW"))
        .andExpect(jsonPath("$.success.data.parent.parentId").value(reply.getDisplayReviewId()))
        .andExpect(jsonPath("$.success.data.displayId").value(1))
        .andExpect(jsonPath("$.success.data.reports[0].reportId").value(first.getReportId()))
        .andExpect(jsonPath("$.success.data.reports[0].reasonCode").value("ABUSE_DEFAMATION"))
        .andExpect(jsonPath("$.success.data.reports[0].description").value("신고 내용"))
        .andExpect(jsonPath("$.success.data.reports[1].reportId").value(second.getReportId()))
        .andExpect(jsonPath("$.success.data.resolution").doesNotExist());
    get(CASES + "/" + second.getReportId())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    get(CASES + "/999999").andExpect(status().isNotFound());
  }

  @Test
  void noActionClosesRoundAndLaterReportStartsNewRound() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);
    report(reply, 102L, 5);

    resolve(first.getReportId(), "NO_ACTION")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.reportCaseId").value(first.getReportId()))
        .andExpect(jsonPath("$.success.data.status").value("RESOLVED"))
        .andExpect(jsonPath("$.success.data.action").value("NO_ACTION"))
        .andExpect(jsonPath("$.success.data.commentType").value("DISPLAY_REVIEW_REPLY"))
        .andExpect(jsonPath("$.success.data.commentId").value(reply.getDisplayReviewReplyId()))
        .andExpect(jsonPath("$.success.data.operatorId").value(adminId));

    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();
    get(CASES + "/" + first.getReportId())
        .andExpect(jsonPath("$.success.data.status").value("RESOLVED"))
        .andExpect(jsonPath("$.success.data.reports.length()").value(2))
        .andExpect(jsonPath("$.success.data.resolution.action").value("NO_ACTION"))
        .andExpect(jsonPath("$.success.data.resolution.reason").value("운영 검토"))
        .andExpect(jsonPath("$.success.data.resolution.operatorId").value(adminId));
    get(CASES + "?status=PENDING").andExpect(jsonPath("$.success.data.items").isEmpty());

    ContentReportJpaEntity next = report(reply, 103L, 20);
    get(CASES + "?status=PENDING")
        .andExpect(jsonPath("$.success.data.items.length()").value(1))
        .andExpect(jsonPath("$.success.data.items[0].reportCaseId").value(next.getReportId()))
        .andExpect(jsonPath("$.success.data.items[0].reportCount").value(1));
    get(CASES + "?status=RESOLVED")
        .andExpect(jsonPath("$.success.data.items.length()").value(1))
        .andExpect(jsonPath("$.success.data.items[0].reportCaseId").value(first.getReportId()))
        .andExpect(jsonPath("$.success.data.items[0].status").value("RESOLVED"))
        .andExpect(jsonPath("$.success.data.items[0].reportCount").value(2));
    get(CASES + "/" + first.getReportId())
        .andExpect(jsonPath("$.success.data.reports.length()").value(2));
  }

  @Test
  void deleteCommentDeletesCommentAndClosesRound() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);

    resolve(first.getReportId(), "DELETE_COMMENT")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.action").value("DELETE_COMMENT"));

    DisplayReviewReply deleted = replies.findById(reply.getDisplayReviewReplyId()).orElseThrow();
    assertThat(deleted.isDeleted()).isTrue();
    AdminReportCaseJpaEntity closed = cases.findById(first.getReportId()).orElseThrow();
    assertThat(closed.getProcessedAt()).isEqualTo(deleted.getDeletedAt());
    assertThat(closed.getReason()).isEqualTo("운영 검토");
    get(CASES + "/" + first.getReportId())
        .andExpect(jsonPath("$.success.data.comment.deletedAt").exists())
        .andExpect(jsonPath("$.success.data.resolution.action").value("DELETE_COMMENT"));
  }

  @Test
  void rejectsDuplicateResolutionAndDeletingAlreadyDeletedComment() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);
    resolve(first.getReportId(), "NO_ACTION").andExpect(status().isOk());

    resolve(first.getReportId(), "NO_ACTION")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    resolve(first.getReportId(), "DELETE_COMMENT").andExpect(status().isConflict());
    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();

    DisplayReviewReply removed = reply();
    removed.delete();
    replies.saveAndFlush(removed);
    ContentReportJpaEntity onRemoved = report(removed, 101L, 1);
    resolve(onRemoved.getReportId(), "DELETE_COMMENT")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    assertThat(cases.existsById(onRemoved.getReportId())).isFalse();
    resolve(onRemoved.getReportId(), "NO_ACTION").andExpect(status().isOk());
    resolve(999_999L, "NO_ACTION").andExpect(status().isNotFound());
  }

  @Test
  void failedClosingRollsBackCommentDeletion() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);
    doThrow(new DataIntegrityViolationException("동시 종결")).when(cases).saveAndFlush(any());

    resolve(first.getReportId(), "DELETE_COMMENT")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONFLICT"));

    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(cases.count()).isZero();
  }

  @Test
  void ordinaryUserCannotReadOrResolveCases() throws Exception {
    DisplayReviewReply reply = reply();
    ContentReportJpaEntity first = report(reply, 101L, 0);
    token = "Bearer " + tokens.createAccessToken(user(UserRole.USER));

    get(CASES + "?status=PENDING").andExpect(status().isForbidden());
    resolve(first.getReportId(), "DELETE_COMMENT").andExpect(status().isForbidden());
    assertThat(replies.findById(reply.getDisplayReviewReplyId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(cases.count()).isZero();
  }

  private ResultActions get(String path) throws Exception {
    return mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(path)
            .header("Authorization", token));
  }

  private ResultActions resolve(Long reportCaseId, String action) throws Exception {
    return mvc.perform(
        post(CASES + "/{id}/resolve", reportCaseId)
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"action\":\"" + action + "\",\"reason\":\"  운영 검토  \"}"));
  }

  private DisplayReviewReply reply() {
    DisplayReview review = reviews.saveAndFlush(DisplayReview.create(1L, 2L, "후기", List.of()));
    return replies.saveAndFlush(
        DisplayReviewReply.create(review.getDisplayReviewId(), 2L, "답글", List.of()));
  }

  private ContentReportJpaEntity report(DisplayReviewReply reply, Long reporterId, int minutes) {
    return reports.saveAndFlush(
        new ContentReportJpaEntity(
            null,
            ReportTargetType.DISPLAY_REVIEW_REPLY,
            reply.getDisplayReviewReplyId(),
            reporterId,
            2L,
            ReportReason.ABUSE_DEFAMATION,
            "신고 내용",
            BASE.plusMinutes(minutes)));
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
