package com.example.demo.domain.user.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserSearchControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserJpaRepository userJpaRepository;
  @Autowired private TokenProvider tokenProvider;

  private String accessToken;

  @BeforeEach
  void setUp() {
    User requester = saveUser("requester", "요청자", true, null);
    accessToken = tokenProvider.createAccessToken(requester);
  }

  @Test
  void returnsSingleUserWhenNicknameMatchesExactly() throws Exception {
    User quietRoom = saveUser("quietroom", "홍길동", false, null);
    saveUser("quietroom2", "다른 사용자", true, null);

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "  quietroom  ")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.userId").value(quietRoom.getId()))
        .andExpect(jsonPath("$.success.data.name").value("홍길동"))
        .andExpect(jsonPath("$.success.data.nickname").value("quietroom"))
        .andExpect(jsonPath("$.success.data.socialEmail").doesNotExist())
        .andExpect(jsonPath("$.success.data.provider").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/users/search"));
  }

  @Test
  void returnsNotFoundWhenOnlyPartialNicknameMatches() throws Exception {
    saveUser("quiet-active", "활성 사용자", false, null);

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "quiet")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("USER_NICKNAME_NOT_FOUND"));
  }

  @Test
  void excludesWithdrawnUserWithExactNickname() throws Exception {
    saveUser("quiet-deleted", "탈퇴 사용자", false, LocalDateTime.now());

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "quiet-deleted")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("USER_NICKNAME_NOT_FOUND"));
  }

  @Test
  void returnsNotFoundWhenNoUserMatches() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "unknown")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("USER_NICKNAME_NOT_FOUND"));
  }

  @Test
  void rejectsBlankNickname() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "   ")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void rejectsRequestWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/search").param("nickname", "quiet"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private User saveUser(String nickname, String name, boolean verified, LocalDateTime deletedAt) {
    String suffix = nickname + "-" + userJpaRepository.count();
    return userJpaRepository.saveAndFlush(
        User.builder()
            .provider(Provider.Google)
            .providerId("provider-" + suffix)
            .name(name)
            .nickname(nickname)
            .isVerified(verified)
            .socialEmail(suffix + "@example.com")
            .deletedAt(deletedAt)
            .build());
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
