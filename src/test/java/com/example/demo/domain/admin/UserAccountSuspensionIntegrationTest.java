package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.AccountStatus;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserAccountSuspensionIntegrationTest {

  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;

  @Test
  void administratorSuspendsQueriesAndReleasesAccount() throws Exception {
    User admin = user(UserRole.ADMIN);
    User target = user(UserRole.USER);
    String adminToken = tokens.createAccessToken(admin);

    mvc.perform(statusRequest(target.getId()).header("Authorization", bearer(adminToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.suspendedAt").doesNotExist())
        .andExpect(jsonPath("$.success.data.operatorId").doesNotExist())
        .andExpect(jsonPath("$.success.data.reason").doesNotExist());

    mvc.perform(
            changeRequest(target.getId(), "SUSPENDED", "운영 정책 위반")
                .header("Authorization", bearer(adminToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.userId").value(target.getId()))
        .andExpect(jsonPath("$.success.data.accountStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.success.data.processedAt").isNotEmpty());

    User suspended = users.findById(target.getId()).orElseThrow();
    assertThat(suspended.getAccountStatus()).isEqualTo(AccountStatus.SUSPENDED);
    assertThat(suspended.getSuspendedAt()).isNotNull();
    assertThat(suspended.getSuspensionOperatorId()).isEqualTo(admin.getId());
    assertThat(suspended.getSuspensionReason()).isEqualTo("운영 정책 위반");

    mvc.perform(statusRequest(target.getId()).header("Authorization", bearer(adminToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.success.data.suspendedAt").isNotEmpty())
        .andExpect(jsonPath("$.success.data.operatorId").value(admin.getId()))
        .andExpect(jsonPath("$.success.data.reason").value("운영 정책 위반"));

    mvc.perform(
            changeRequest(target.getId(), "SUSPENDED", "중복 요청")
                .header("Authorization", bearer(adminToken)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ALREADY_SUSPENDED_USER"));

    mvc.perform(
            changeRequest(target.getId(), "ACTIVE", "관리자 검토 완료")
                .header("Authorization", bearer(adminToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("ACTIVE"));

    User active = users.findById(target.getId()).orElseThrow();
    assertThat(active.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    assertThat(active.getSuspendedAt()).isNull();
    assertThat(active.getSuspensionOperatorId()).isNull();
    assertThat(active.getSuspensionReason()).isNull();

    mvc.perform(
            changeRequest(target.getId(), "ACTIVE", "중복 요청")
                .header("Authorization", bearer(adminToken)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ALREADY_ACTIVE_USER"));
  }

  @Test
  void returnsNotFoundForUnknownUser() throws Exception {
    User admin = user(UserRole.ADMIN);
    mvc.perform(
            statusRequest(Long.MAX_VALUE)
                .header("Authorization", bearer(tokens.createAccessToken(admin))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
  }

  @Test
  void accessTokenIssuedBeforeSuspensionIsBlockedAndWorksAfterRelease() throws Exception {
    User user = user(UserRole.USER);
    String accessToken = tokens.createAccessToken(user);
    user.suspend(LocalDateTime.now(ZoneOffset.UTC), 999L, "운영 정책 위반");
    users.flush();

    mvc.perform(get("/api/v1/users/me").header("Authorization", bearer(accessToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("ACCOUNT_SUSPENDED"));

    mvc.perform(get("/api/v1/health").header("Authorization", bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.status").value("UP"));

    user.releaseSuspension();
    users.flush();

    mvc.perform(get("/api/v1/users/me").header("Authorization", bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.id").value(user.getId()));
  }

  @Test
  void withdrawalAndSuspensionRemainDistinctStates() throws Exception {
    User admin = user(UserRole.ADMIN);
    User target = user(UserRole.USER);
    target.withdraw(LocalDateTime.now(ZoneOffset.UTC));
    users.flush();

    assertThat(target.getDeletedAt()).isNotNull();
    assertThat(target.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    mvc.perform(
            changeRequest(target.getId(), "SUSPENDED", "운영 정책 위반")
                .header("Authorization", bearer(tokens.createAccessToken(admin))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ALREADY_WITHDRAWN_USER"));
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder statusRequest(
      Long userId) {
    return get("/api/v1/admin/users/{userId}/account-status", userId);
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder changeRequest(
      Long userId, String accountStatus, String reason) {
    return patch("/api/v1/admin/users/{userId}/account-status", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountStatus\":\"" + accountStatus + "\",\"reason\":\"" + reason + "\"}");
  }

  private String bearer(String token) {
    return "Bearer " + token;
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
