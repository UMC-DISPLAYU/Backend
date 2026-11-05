package com.example.demo.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.ContentStatusPort;
import com.example.demo.domain.admin.application.result.ContentStatusResult;
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
import org.junit.jupiter.params.provider.CsvSource;
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

@SpringBootTest(properties = "admin.content-status.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminContentStatusAuthorizationIntegrationTest {
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private ContentStatusPort changes;

  @ParameterizedTest
  @CsvSource({"DISPLAY,ACTIVE", "DISPLAY,SUSPENDED", "ARTWORK,ACTIVE", "ARTWORK,SUSPENDED"})
  void administratorCanChangeBothContentTypes(String type, String status) throws Exception {
    User admin = user(UserRole.ADMIN);
    when(changes.changeStatus(type, 10L, admin.getId(), status, "운영 검토"))
        .thenReturn(
            new ContentStatusResult(
                type,
                10L,
                status,
                "ARTWORK".equals(type) ? "ACTIVE" : null,
                false,
                List.of("SCHEDULED"),
                Instant.parse("2026-11-05T13:00:00Z")));
    mvc.perform(
            request(type, status)
                .header("Authorization", "Bearer " + tokens.createAccessToken(admin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.contentType").value(type))
        .andExpect(jsonPath("$.success.data.operationStatus").value(status));
    verify(changes).changeStatus(type, 10L, admin.getId(), status, "운영 검토");
  }

  @Test
  void anonymousAndOrdinaryUsersCannotChangeStatus() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String type : List.of("DISPLAY", "ARTWORK")) {
      mvc.perform(request(type, "SUSPENDED")).andExpect(status().isUnauthorized());
      mvc.perform(request(type, "ACTIVE").header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(changes);
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
    for (String type : List.of("DISPLAY", "ARTWORK")) {
      mvc.perform(request(type, "SUSPENDED").header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(changes);
  }

  @Test
  void invalidAndRefreshTokensCannotChangeStatus() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      for (String type : List.of("DISPLAY", "ARTWORK")) {
        mvc.perform(request(type, "SUSPENDED").header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
      }
    }
    verifyNoInteractions(changes);
  }

  private MockHttpServletRequestBuilder request(String type, String status) {
    return patch("/api/v1/admin/contents/{contentType}/{contentId}/operation-status", type, 10)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"operationStatus\":\"" + status + "\",\"reason\":\"운영 검토\"}");
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
