package com.example.demo.domain.user.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
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
  void searchesByFullAndPartialNicknameIgnoringCaseAndIncludesUnverifiedUsers() throws Exception {
    User quietRoom = saveUser("quietroom", "이정우", false, null);
    User quietRoomTwo = saveUser("QuietRoom2", "최유성", true, null);
    saveUser("another", "다른 사용자", true, null);

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "  QUIETROOM  ")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.length()").value(2))
        .andExpect(jsonPath("$.success.data[0].userId").value(quietRoomTwo.getId()))
        .andExpect(jsonPath("$.success.data[0].name").value("최유성"))
        .andExpect(jsonPath("$.success.data[0].nickname").value("QuietRoom2"))
        .andExpect(jsonPath("$.success.data[1].userId").value(quietRoom.getId()))
        .andExpect(jsonPath("$.success.data[1].name").value("이정우"))
        .andExpect(jsonPath("$.success.data[1].nickname").value("quietroom"))
        .andExpect(jsonPath("$.success.data[0].socialEmail").doesNotExist())
        .andExpect(jsonPath("$.success.data[0].provider").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/users/search"));
  }

  @Test
  void excludesWithdrawnUsers() throws Exception {
    saveUser("quiet-active", "활성 사용자", false, null);
    saveUser("quiet-deleted", "탈퇴 사용자", false, LocalDateTime.now());

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "quiet")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.length()").value(1))
        .andExpect(jsonPath("$.success.data[0].nickname").value("quiet-active"));
  }

  @Test
  void sortsSameNicknamesByUserId() throws Exception {
    User first = saveUser("sameNickname", "첫 번째", false, null);
    User second = saveUser("sameNickname", "두 번째", true, null);

    mockMvc
        .perform(
            get("/api/v1/users/search")
                .param("nickname", "sameNickname")
                .header(HttpHeaders.AUTHORIZATION, bearer(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data[0].userId").value(first.getId()))
        .andExpect(jsonPath("$.success.data[1].userId").value(second.getId()));
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
        .andExpect(jsonPath("$.error.code").value("USER_NICKNAME_NOT_FOUND"))
        .andExpect(jsonPath("$.error.message").value("해당 닉네임을 가진 사용자를 찾을 수 없습니다."));
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
