package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.admin.application.port.UserQueryPort;
import com.example.demo.domain.admin.application.service.AdminUserService;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.AdminUserQueryAdapter;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {"admin.users.enabled=true", "admin.user-status.enabled=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminUserQueryIntegrationTest {

  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private UserQueryPort userQueries;
  @Autowired private AdminUserService adminUserService;

  @Test
  void enabledAdminApiUsesRealUserQueryAdapterAndKeepsResponseContract() throws Exception {
    User admin = user(UserRole.ADMIN, "admin", "admin@example.com");
    User target = user(UserRole.USER, "target-artist", "target@example.com");
    String authorization = "Bearer " + tokens.createAccessToken(admin);

    assertThat(userQueries).isInstanceOf(AdminUserQueryAdapter.class);
    assertThat(adminUserService).isNotNull();

    mvc.perform(
            get("/api/v1/admin/users")
                .header("Authorization", authorization)
                .param("q", "target@example")
                .param("accountStatus", "ACTIVE")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].userId").value(target.getId()))
        .andExpect(jsonPath("$.success.data.items[0].nickname").value("target-artist"))
        .andExpect(jsonPath("$.success.data.items[0].email").value("target@example.com"))
        .andExpect(jsonPath("$.success.data.items[0].accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.items[0].createdAt").isString())
        .andExpect(jsonPath("$.success.data.nextCursor").value(nullValue()))
        .andExpect(jsonPath("$.success.data.hasNext").value(false));

    mvc.perform(
            get("/api/v1/admin/users/{userId}", target.getId())
                .header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.userId").value(target.getId()))
        .andExpect(jsonPath("$.success.data.nickname").value("target-artist"))
        .andExpect(jsonPath("$.success.data.email").value("target@example.com"))
        .andExpect(jsonPath("$.success.data.accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.suspendedAt").value(nullValue()))
        .andExpect(jsonPath("$.success.data.operatorId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.reason").value(nullValue()))
        .andExpect(jsonPath("$.success.data.history").isEmpty());
  }

  @Test
  void statusChangesAreImmediatelyReflectedInUserListAndDetail() throws Exception {
    User admin = user(UserRole.ADMIN, "admin", "admin@example.com");
    User target = user(UserRole.USER, "target", "target@example.com");
    String authorization = "Bearer " + tokens.createAccessToken(admin);

    mvc.perform(
            patch("/api/v1/admin/users/{userId}/account-status", target.getId())
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountStatus\":\"SUSPENDED\",\"reason\":\"policy violation\"}"))
        .andExpect(status().isOk());

    mvc.perform(
            get("/api/v1/admin/users")
                .header("Authorization", authorization)
                .param("accountStatus", "SUSPENDED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].userId").value(target.getId()))
        .andExpect(jsonPath("$.success.data.items[0].accountStatus").value("SUSPENDED"));
    mvc.perform(
            get("/api/v1/admin/users/{userId}", target.getId())
                .header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.success.data.suspendedAt").isString())
        .andExpect(jsonPath("$.success.data.operatorId").value(admin.getId()))
        .andExpect(jsonPath("$.success.data.reason").value("policy violation"));

    mvc.perform(
            patch("/api/v1/admin/users/{userId}/account-status", target.getId())
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountStatus\":\"ACTIVE\",\"reason\":\"review completed\"}"))
        .andExpect(status().isOk());

    mvc.perform(
            get("/api/v1/admin/users/{userId}", target.getId())
                .header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.suspendedAt").value(nullValue()))
        .andExpect(jsonPath("$.success.data.operatorId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.reason").value(nullValue()));
  }

  private User user(UserRole role, String nickname, String email) {
    return users.saveAndFlush(
        User.builder()
            .provider(Provider.Kakao)
            .providerId(UUID.randomUUID().toString())
            .name(nickname)
            .nickname(nickname)
            .socialEmail(email)
            .role(role)
            .build());
  }
}
