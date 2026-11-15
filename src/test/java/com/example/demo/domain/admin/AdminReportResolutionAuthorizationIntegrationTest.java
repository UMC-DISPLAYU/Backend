package com.example.demo.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.ReportResolutionPort;
import com.example.demo.domain.admin.application.result.ReportResolutionResult;
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

@SpringBootTest(properties = "admin.report-resolution.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminReportResolutionAuthorizationIntegrationTest {
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private ReportResolutionPort deletions;

  @ParameterizedTest
  @ValueSource(strings = {"NO_ACTION", "DELETE_COMMENT"})
  void administratorCanResolveBothActions(String type) throws Exception {
    User admin = user(UserRole.ADMIN);
    when(deletions.resolve(10L, type, admin.getId(), "운영 검토"))
        .thenReturn(
            new ReportResolutionResult(
                10L,
                "RESOLVED",
                type,
                "COMMENT_KIND",
                401L,
                admin.getId(),
                Instant.parse("2026-11-05T13:00:00Z")));
    mvc.perform(request(type).header("Authorization", "Bearer " + tokens.createAccessToken(admin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.action").value(type))
        .andExpect(jsonPath("$.success.data.reportCaseId").value(10));
    verify(deletions).resolve(10L, type, admin.getId(), "운영 검토");
  }

  @Test
  void anonymousAndOrdinaryUsersCannotResolve() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String type : List.of("NO_ACTION", "DELETE_COMMENT")) {
      mvc.perform(request(type)).andExpect(status().isUnauthorized());
      mvc.perform(request(type).header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(deletions);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void revokedOrWithdrawnAdministratorsCannotUseExistingTokens(boolean withdraw) throws Exception {
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
    for (String type : List.of("NO_ACTION", "DELETE_COMMENT")) {
      mvc.perform(request(type).header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(deletions);
  }

  @Test
  void invalidAndRefreshTokensCannotResolve() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      for (String type : List.of("NO_ACTION", "DELETE_COMMENT")) {
        mvc.perform(request(type).header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
      }
    }
    verifyNoInteractions(deletions);
  }

  private MockHttpServletRequestBuilder request(String type) {
    return post("/api/v1/admin/report-cases/{reportCaseId}/resolve", 10)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"action\":\"" + type + "\",\"reason\":\"운영 검토\"}");
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
