package com.example.demo.domain.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.ParentQueryPort;
import com.example.demo.domain.admin.application.port.ReportCaseQueryPort;
import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.ParentDetail;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "admin.report-cases.enabled=true",
      "admin.comments.enabled=true",
      "admin.parents.enabled=true"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminReportCaseAuthorizationIntegrationTest {
  private static final List<String> PATHS =
      List.of(
          "/api/v1/admin/report-cases",
          "/api/v1/admin/report-cases/10",
          "/api/v1/admin/comments/COMMENT_KIND/41",
          "/api/v1/admin/parents/PARENT_KIND/31");
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private ReportCaseQueryPort queries;
  @MockitoBean private CommentQueryPort comments;
  @MockitoBean private CommentDeletionPort deletions;
  @MockitoBean private ParentQueryPort parents;

  @Test
  void administratorCanReadBothEndpointsUsingRealAuthorization() throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    when(queries.search(any())).thenReturn(new ReportCasePage(List.of(), null, false));
    when(queries.getDetail(10L))
        .thenReturn(
            new ReportCaseDetail(
                10L,
                "PENDING",
                "COMMENT_KIND",
                401L,
                1L,
                Instant.parse("2026-11-02T12:00:00Z"),
                null,
                null,
                null,
                null,
                List.of(),
                null));
    when(comments.getCommentDetail("COMMENT_KIND", 41L))
        .thenReturn(
            new CommentDetail(
                new CommentDetail.Comment(
                    "COMMENT_KIND", 41L, null, null, null, List.of(), null, null),
                null,
                null,
                null));
    when(deletions.delete("DISPLAY_REVIEW_REPLY", 41L, admin.getId(), "운영 검토"))
        .thenReturn(new CommentDeletionResult(41L, Instant.parse("2026-11-17T01:00:00Z")));
    when(parents.getParentDetail("PARENT_KIND", 31L))
        .thenReturn(
            new ParentDetail(
                new ParentDetail.Parent(
                    "PARENT_KIND", 31L, null, null, null, List.of(), null, null),
                null,
                null));

    mvc.perform(get(PATHS.get(0)).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isEmpty());
    mvc.perform(get(PATHS.get(1)).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.reportCaseId").value(10));
    mvc.perform(get(PATHS.get(2)).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comment.commentId").value(41));
    mvc.perform(get(PATHS.get(3)).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.parent.parentId").value(31));
    mvc.perform(
            post("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41/delete")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"운영 검토\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.commentId").value(41));
    verify(queries).search(any());
    verify(queries).getDetail(10L);
    verify(comments).getCommentDetail("COMMENT_KIND", 41L);
    verify(deletions).delete("DISPLAY_REVIEW_REPLY", 41L, admin.getId(), "운영 검토");
    verify(parents).getParentDetail("PARENT_KIND", 31L);
  }

  @Test
  void anonymousAndOrdinaryUsersCannotReadEitherEndpoint() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String path : PATHS) {
      mvc.perform(get(path)).andExpect(status().isUnauthorized());
      mvc.perform(get(path).header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    mvc.perform(deletionRequest()).andExpect(status().isUnauthorized());
    mvc.perform(deletionRequest().header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    verifyNoInteractions(queries, comments, deletions, parents);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void revokedOrWithdrawnAdministratorsCannotUsePreviouslyIssuedTokens(boolean withdraw)
      throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    if (withdraw) {
      admin.withdraw(LocalDateTime.now(ZoneOffset.UTC));
      users.flush();
    } else {
      entityManager
          .createQuery("update User u set u.role = :role where u.id = :id")
          .setParameter("role", UserRole.USER)
          .setParameter("id", admin.getId())
          .executeUpdate();
    }
    entityManager.clear();

    for (String path : PATHS) {
      mvc.perform(get(path).header("Authorization", "Bearer " + token))
          .andExpect(status().is(withdraw ? 401 : 403));
    }
    mvc.perform(deletionRequest().header("Authorization", "Bearer " + token))
        .andExpect(status().is(withdraw ? 401 : 403));
    verifyNoInteractions(queries, comments, deletions, parents);
  }

  @Test
  void invalidAndRefreshTokensCannotReadEitherEndpoint() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      for (String path : PATHS) {
        mvc.perform(get(path).header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
      }
      mvc.perform(deletionRequest().header("Authorization", "Bearer " + token))
          .andExpect(status().isUnauthorized());
    }
    verifyNoInteractions(queries, comments, deletions, parents);
  }

  private MockHttpServletRequestBuilder deletionRequest() {
    return post("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41/delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"reason\":\"운영 검토\"}");
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
