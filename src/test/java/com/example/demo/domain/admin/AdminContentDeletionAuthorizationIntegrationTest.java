package com.example.demo.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.ContentDeletionPort;
import com.example.demo.domain.admin.application.result.ContentDeletionResult;
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

@SpringBootTest(properties = "admin.content-deletion.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminContentDeletionAuthorizationIntegrationTest {
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private ContentDeletionPort deletions;

  @ParameterizedTest
  @ValueSource(strings = {"DISPLAY", "ARTWORK"})
  void administratorCanDeleteBothContentTypes(String type) throws Exception {
    User admin = user(UserRole.ADMIN);
    when(deletions.delete(type, 10L, admin.getId(), "운영 검토"))
        .thenReturn(new ContentDeletionResult(type, 10L, Instant.parse("2026-11-05T13:00:00Z")));
    mvc.perform(request(type).header("Authorization", "Bearer " + tokens.createAccessToken(admin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.deletedTargetType").value(type))
        .andExpect(jsonPath("$.success.data.deletedTargetId").value(10));
    verify(deletions).delete(type, 10L, admin.getId(), "운영 검토");
  }

  @Test
  void anonymousAndOrdinaryUsersCannotDelete() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String type : List.of("DISPLAY", "ARTWORK")) {
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
    for (String type : List.of("DISPLAY", "ARTWORK")) {
      mvc.perform(request(type).header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(deletions);
  }

  @Test
  void invalidAndRefreshTokensCannotDelete() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      for (String type : List.of("DISPLAY", "ARTWORK")) {
        mvc.perform(request(type).header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
      }
    }
    verifyNoInteractions(deletions);
  }

  private MockHttpServletRequestBuilder request(String type) {
    return post("/api/v1/admin/contents/{contentType}/{contentId}/delete", type, 10)
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
