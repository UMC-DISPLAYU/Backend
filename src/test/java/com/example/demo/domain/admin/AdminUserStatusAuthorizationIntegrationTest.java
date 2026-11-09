package com.example.demo.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.result.UserStatusResult;
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

@SpringBootTest(properties = "admin.user-status.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminUserStatusAuthorizationIntegrationTest {
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private UserStatusPort changes;

  @ParameterizedTest
  @ValueSource(strings = {"SUSPENDED", "ACTIVE"})
  void administratorCanChangeOrdinaryUserStatus(String status) throws Exception {
    User admin = user(UserRole.ADMIN);
    User target = user(UserRole.USER);
    Instant processedAt = Instant.parse("2026-11-05T09:00:00Z");
    when(changes.changeStatus(target.getId(), admin.getId(), status, "운영 검토"))
        .thenReturn(new UserStatusResult(target.getId(), status, processedAt));
    mvc.perform(
            request(target.getId(), status)
                .header("Authorization", "Bearer " + tokens.createAccessToken(admin)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value(status));
    verify(changes).changeStatus(target.getId(), admin.getId(), status, "운영 검토");
  }

  @Test
  void anonymousAndOrdinaryUsersCannotChangeStatus() throws Exception {
    User target = user(UserRole.USER);
    mvc.perform(request(target.getId(), "SUSPENDED")).andExpect(status().isUnauthorized());
    mvc.perform(
            request(target.getId(), "SUSPENDED")
                .header("Authorization", "Bearer " + tokens.createAccessToken(target)))
        .andExpect(status().isForbidden());
    verifyNoInteractions(changes);
  }

  @Test
  void administratorCannotChangeSelfOrOtherAdministratorStatus() throws Exception {
    User admin = user(UserRole.ADMIN);
    User other = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    for (Long target : List.of(admin.getId(), other.getId())) {
      for (String status : List.of("ACTIVE", "SUSPENDED")) {
        mvc.perform(request(target, status).header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
      }
    }
    verifyNoInteractions(changes);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void revokedOrWithdrawnAdministratorsCannotUseExistingTokens(boolean withdraw) throws Exception {
    User admin = user(UserRole.ADMIN);
    User target = user(UserRole.USER);
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
    mvc.perform(request(target.getId(), "SUSPENDED").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    verifyNoInteractions(changes);
  }

  @Test
  void invalidAndRefreshTokensCannotChangeStatus() throws Exception {
    User admin = user(UserRole.ADMIN);
    User target = user(UserRole.USER);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      mvc.perform(request(target.getId(), "SUSPENDED").header("Authorization", "Bearer " + token))
          .andExpect(status().isUnauthorized());
    }
    verifyNoInteractions(changes);
  }

  private MockHttpServletRequestBuilder request(Long userId, String status) {
    return patch("/api/v1/admin/users/{userId}/account-status", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountStatus\":\"" + status + "\",\"reason\":\"운영 검토\"}");
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
