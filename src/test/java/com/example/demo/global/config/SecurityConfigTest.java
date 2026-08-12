package com.example.demo.global.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void rejectsAccessTokenRefreshWithoutRefreshTokenCookie() throws Exception {
    mockMvc.perform(post("/api/v1/auth/refresh")).andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/api/v1/users/search",
        "/api/v1/lounge/me/posts",
        "/api/v1/display/me",
        "/api/v1/display-invitations",
        "/api/v1/display-invitations/me",
        "/api/v1/artworks/1/edit",
        "/api/v1/artworks/questions/me",
        "/api/v1/artworks/questions/received",
        "/api/v1/artworks/feelings/me",
        "/api/v1/display/reviews/me"
      })
  void rejectsHeadRequestWithoutAuthentication(String path) throws Exception {
    mockMvc.perform(head(path)).andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/v1/display/1/reviews", "/api/v1/display/1/reviews/1/replies"})
  void permitsDisplayReviewHeadRequestWithoutAuthentication(String path) throws Exception {
    mockMvc
        .perform(head(path))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
  }

  @Test
  void permitsDisplayMembersRequestWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/1/members"))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    mockMvc
        .perform(head("/api/v1/display/1/members"))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/api/v1/auth/kakao/login-url",
        "/api/v1/auth/kakao/callback",
        "/api/v1/auth/google/login-url",
        "/api/v1/auth/google/callback"
      })
  void permitsOAuthRequestWithoutAuthentication(String path) throws Exception {
    mockMvc
        .perform(get(path))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
  }
}
