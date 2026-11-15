package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.display.application.cache.DisplayCacheNames;
import com.example.demo.domain.display.infrastructure.persistence.DuPickColumnJpaEntity;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDuPickQueryJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminDuPickCropIntegrationTest {
  private static final long COLUMN_ID = 900263L;
  private static final String PATH = "/api/v1/admin/du-picks/" + COLUMN_ID + "/poster-crop";
  private static final String CROP =
      "{\"posterCrop\":{\"x\":0.1234567891,\"y\":0.2,\"w\":0.5,\"h\":0.8}}";
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private SpringDataDuPickQueryJpaRepository columns;
  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private CacheManager caches;
  @Autowired private EntityManager entityManager;
  private final List<Long> userIds = new ArrayList<>();
  private String adminToken;

  @BeforeEach
  void setUp() {
    caches.getCache(DisplayCacheNames.DU_PICKS).clear();
    columns.saveAndFlush(
        new DuPickColumnJpaEntity(
            COLUMN_ID,
            "배너",
            "설명",
            "https://cdn.example.com/banner.jpg",
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 1, 1, 0, 0)));
    adminToken = tokens.createAccessToken(user(UserRole.ADMIN));
  }

  @AfterEach
  void tearDown() {
    columns.deleteById(COLUMN_ID);
    users.deleteAllById(userIds);
    caches.getCache(DisplayCacheNames.DU_PICKS).clear();
  }

  @Test
  void adminWritesPreciseCropAndPublicCachedReadReflectsChangeAndClear() throws Exception {
    read().andExpect(jsonPath("$.success.data.duPicks[0].posterCrop").doesNotExist());
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isOk());
    read()
        .andExpect(jsonPath("$.success.data.duPicks[0].posterCrop.x").value(0.1234567891))
        .andExpect(jsonPath("$.success.data.duPicks[0].posterCrop.y").value(0.2))
        .andExpect(jsonPath("$.success.data.duPicks[0].posterCrop.w").value(0.5))
        .andExpect(jsonPath("$.success.data.duPicks[0].posterCrop.h").value(0.8));
    assertThat(
            entityManager
                .createQuery(
                    "select c.posterCrop.x from DuPickColumnJpaEntity c where c.id = :id",
                    java.math.BigDecimal.class)
                .setParameter("id", COLUMN_ID)
                .getSingleResult())
        .isEqualByComparingTo("0.1234567891");
    assertThat(
            entityManager
                .createQuery(
                    "select c.updatedAt from DuPickColumnJpaEntity c where c.id = :id",
                    LocalDateTime.class)
                .setParameter("id", COLUMN_ID)
                .getSingleResult())
        .isAfter(LocalDateTime.of(2026, 1, 1, 0, 0));
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"posterCrop\":null}"))
        .andExpect(status().isOk());
    read().andExpect(jsonPath("$.success.data.duPicks[0].posterCrop").doesNotExist());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"x\":-0.1,\"y\":0,\"w\":1,\"h\":1}",
        "{\"x\":0,\"y\":0,\"w\":0,\"h\":1}",
        "{\"x\":0.5,\"y\":0,\"w\":0.6,\"h\":1}",
        "{\"x\":0,\"y\":0.8,\"w\":1,\"h\":0.3}",
        "{\"x\":0.00000000001,\"y\":0,\"w\":0.5,\"h\":1}",
        "{\"x\":0,\"y\":0,\"w\":1}"
      })
  void invalidCropIsRejectedWithoutChangingStoredCrop(String crop) throws Exception {
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isOk());
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"posterCrop\":" + crop + "}"))
        .andExpect(status().isBadRequest());
    read().andExpect(jsonPath("$.success.data.duPicks[0].posterCrop.x").value(0.1234567891));
  }

  @Test
  void anonymousOrdinaryAndRevokedAdminCannotWrite() throws Exception {
    mvc.perform(patch(PATH).contentType(MediaType.APPLICATION_JSON).content(CROP))
        .andExpect(status().isUnauthorized());
    String token = tokens.createAccessToken(user(UserRole.USER));
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isForbidden());
    new TransactionTemplate(transactionManager)
        .executeWithoutResult(
            status ->
                entityManager
                    .createQuery("update User u set u.role = :role where u.id = :id")
                    .setParameter("role", UserRole.USER)
                    .setParameter("id", userIds.getFirst())
                    .executeUpdate());
    mvc.perform(
            patch(PATH)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isForbidden());
    read().andExpect(jsonPath("$.success.data.duPicks[0].posterCrop").doesNotExist());
  }

  @Test
  void missingBannerReturnsNotFoundAndInvalidIdReturnsBadRequest() throws Exception {
    mvc.perform(
            patch("/api/v1/admin/du-picks/900264/poster-crop")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isNotFound());
    mvc.perform(
            patch("/api/v1/admin/du-picks/0/poster-crop")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CROP))
        .andExpect(status().isBadRequest());
  }

  private org.springframework.test.web.servlet.ResultActions read() throws Exception {
    return mvc.perform(
            get("/api/v1/display/du-picks")
                .param("cursor", String.valueOf(COLUMN_ID - 1))
                .param("size", "1"))
        .andExpect(status().isOk());
  }

  private User user(UserRole role) {
    String key = UUID.randomUUID().toString();
    User user =
        new TransactionTemplate(transactionManager)
            .execute(
                status ->
                    users.saveAndFlush(
                        User.builder()
                            .provider(Provider.Kakao)
                            .providerId(key)
                            .name("사용자")
                            .nickname(key)
                            .socialEmail(key + "@example.com")
                            .role(role)
                            .build()));
    userIds.add(user.getId());
    return user;
  }
}
