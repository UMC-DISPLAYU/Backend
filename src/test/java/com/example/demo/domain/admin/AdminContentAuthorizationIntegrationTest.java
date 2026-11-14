package com.example.demo.domain.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.ContentQueryPort;
import com.example.demo.domain.admin.application.result.ContentDetail;
import com.example.demo.domain.admin.application.result.ContentPage;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "admin.contents.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminContentAuthorizationIntegrationTest {
  private static final List<String> PATHS =
      List.of(
          "/api/v1/admin/contents",
          "/api/v1/admin/contents/DISPLAY/10",
          "/api/v1/admin/contents/ARTWORK/10");
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private ContentQueryPort queries;

  @Test
  void administratorCanReadListAndBothContentTypesUsingRealAuthorization() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.ADMIN));
    when(queries.search(any())).thenReturn(new ContentPage(List.of(), null, false));
    mvc.perform(get(PATHS.getFirst()).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isEmpty());
    for (String type : List.of("DISPLAY", "ARTWORK")) {
      when(queries.getDetail(type, 10L))
          .thenReturn(
              new ContentDetail(
                  type,
                  10L,
                  "콘텐츠",
                  null,
                  null,
                  "SUSPENDED",
                  null,
                  false,
                  List.of("SUSPENDED"),
                  Instant.parse("2026-11-02T12:00:00Z"),
                  null,
                  "소개",
                  List.of(),
                  List.of()));
      mvc.perform(
              get("/api/v1/admin/contents/" + type + "/10")
                  .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.contentType").value(type))
          .andExpect(jsonPath("$.success.data.viewerVisible").value(false));
      verify(queries).getDetail(type, 10L);
    }
    verify(queries).search(any());
  }

  @Test
  void anonymousAndOrdinaryUsersCannotReadAnyContentEndpoint() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String path : PATHS) {
      mvc.perform(get(path)).andExpect(status().isUnauthorized());
      mvc.perform(get(path).header("Authorization", "Bearer " + token))
          .andExpect(status().isForbidden());
    }
    verifyNoInteractions(queries);
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
    verifyNoInteractions(queries);
  }

  @Test
  void invalidAndRefreshTokensCannotReadAnyContentEndpoint() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      for (String path : PATHS) {
        mvc.perform(get(path).header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
      }
    }
    verifyNoInteractions(queries);
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
