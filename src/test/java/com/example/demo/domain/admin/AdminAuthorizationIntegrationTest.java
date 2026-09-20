package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.type.*;
import com.example.demo.domain.display.domain.vo.*;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayScreeningJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.entity.RefreshToken;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.RefreshTokenJpaRepository;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "admin.review.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminAuthorizationIntegrationTest {
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private RefreshTokenJpaRepository refreshTokens;
  @Autowired private SpringDataDisplayJpaRepository displays;
  @Autowired private SpringDataDisplayScreeningJpaRepository screenings;
  @Autowired private EntityManager entityManager;

  @Test
  void administratorCanListReadApproveAndRejectUsingRealAdapters() throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    Display approved = pending(admin.getId(), "승인 전시");
    Display rejected = pending(admin.getId(), "반려 전시");
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isArray());
    mvc.perform(
            get("/api/v1/admin/displays/" + approved.getId())
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(approved.getId()));
    mvc.perform(
            post("/api/v1/admin/displays/" + approved.getId() + "/approve")
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk());
    mvc.perform(
            post("/api/v1/admin/displays/" + rejected.getId() + "/reject")
                .header("Authorization", bearer(token))
                .contentType("application/json")
                .content("{\"reason\":\"일정 수정\"}"))
        .andExpect(status().isOk());
    entityManager.flush();
    entityManager.clear();
    assertThat(displays.findById(approved.getId()).orElseThrow().getStatus())
        .isEqualTo(DisplayStatus.PUBLISHED);
    assertThat(displays.findById(rejected.getId()).orElseThrow().getStatus())
        .isEqualTo(DisplayStatus.REJECTED);
  }

  @Test
  void anonymousAndOrdinaryUsersCannotAccessAnyReviewOperation() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    for (String operation : List.of("list", "detail", "approve", "reject")) {
      mvc.perform(request(operation)).andExpect(status().isUnauthorized());
      mvc.perform(request(operation).header("Authorization", bearer(token)))
          .andExpect(status().isForbidden());
    }
    mvc.perform(get("/api/v1/users/me").header("Authorization", bearer(token)))
        .andExpect(status().isOk());
  }

  @Test
  void currentDatabaseRoleOverridesPreviouslyIssuedToken() throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(token)))
        .andExpect(status().isOk());
    changeRole(admin.getId(), UserRole.USER);
    for (String operation : List.of("list", "detail", "approve", "reject")) {
      mvc.perform(request(operation).header("Authorization", bearer(token)))
          .andExpect(status().isForbidden());
    }
    changeRole(admin.getId(), UserRole.ADMIN);
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(token)))
        .andExpect(status().isOk());
  }

  @Test
  void withdrawnAndMissingUsersCannotUseOldTokens() throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    admin.withdraw(LocalDateTime.now(ZoneOffset.UTC));
    users.flush();
    entityManager.clear();
    for (String operation : List.of("list", "detail", "approve", "reject")) {
      mvc.perform(request(operation).header("Authorization", bearer(token)))
          .andExpect(status().isForbidden());
    }
    String missing = tokens.createAccessToken(User.builder().id(Long.MAX_VALUE).build());
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(missing)))
        .andExpect(status().isForbidden());
  }

  @Test
  void refreshUsesCurrentRoleAndRejectsWithdrawnAccount() throws Exception {
    User admin = user(UserRole.ADMIN);
    String refresh = tokens.createRefreshToken(admin);
    refreshTokens.saveAndFlush(RefreshToken.builder().user(admin).refreshToken(refresh).build());
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(refresh(refresh))))
        .andExpect(status().isOk());
    changeRole(admin.getId(), UserRole.USER);
    String ordinaryToken = refresh(refresh);
    mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(ordinaryToken)))
        .andExpect(status().isForbidden());
    mvc.perform(get("/api/v1/users/me").header("Authorization", bearer(ordinaryToken)))
        .andExpect(status().isOk());
    users.findById(admin.getId()).orElseThrow().withdraw(LocalDateTime.now(ZoneOffset.UTC));
    users.flush();
    entityManager.clear();
    mvc.perform(post("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", refresh)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("WITHDRAWAL_USER"));
  }

  @Test
  void invalidAndRefreshTokensCannotAuthenticateAdminRequests() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      mvc.perform(get("/api/v1/admin/displays").header("Authorization", bearer(token)))
          .andExpect(status().isUnauthorized());
    }
  }

  private String refresh(String token) throws Exception {
    String body =
        mvc.perform(post("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", token)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return JsonPath.read(body, "$.success.data.accessToken");
  }

  private void changeRole(Long userId, UserRole role) {
    entityManager.flush();
    entityManager
        .createQuery("update User u set u.role = :role where u.id = :id")
        .setParameter("role", role)
        .setParameter("id", userId)
        .executeUpdate();
    entityManager.clear();
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

  private Display pending(Long ownerId, String title) {
    Display display =
        Display.create(
            new UserId(ownerId),
            title,
            "https://example.com/poster.png",
            "부제",
            "내용",
            new DisplayLocation("전시장", new BigDecimal("37.5"), new BigDecimal("126.9")),
            "",
            "",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            new DisplayPeriod(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 7),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.requestReview();
    displays.saveAndFlush(display);
    screenings.saveAndFlush(
        DisplayScreening.request(display, ownerId, LocalDateTime.now(ZoneOffset.UTC)));
    return display;
  }

  private MockHttpServletRequestBuilder request(String operation) {
    return switch (operation) {
      case "list" -> get("/api/v1/admin/displays");
      case "detail" -> get("/api/v1/admin/displays/1");
      case "approve" -> post("/api/v1/admin/displays/1/approve");
      default ->
          post("/api/v1/admin/displays/1/reject")
              .contentType("application/json")
              .content("{\"reason\":\"일정 수정\"}");
    };
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
